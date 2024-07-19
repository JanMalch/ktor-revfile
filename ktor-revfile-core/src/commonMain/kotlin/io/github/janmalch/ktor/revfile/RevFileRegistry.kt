package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import io.ktor.http.content.*

/**
 * Read access for a revisioned file registry.
 *
 * @see RevFileRegistry
 */
interface ReadableRevFileRegistry : Iterable<RevisionedFile> {
    /** Returns the number of registered files. */
    fun size(): Int

    /**
     * Returns the revisioned file associated with the given path,
     * or `null` if no such file exists.
     */
    operator fun get(path: String): RevisionedFile?
}

/**
 * Write access for a revisioned file registry.
 *
 * @see RevFileRegistry
 */
interface WriteableRevFileRegistry : ReadableRevFileRegistry {
    /**
     * Registers the given entry and returns its associated revisioned file.
     */
    fun register(entry: Entry): RevisionedFile

    /**
     * Describes registration data to create a revisioned file from.
     */
    interface Entry {
        /**
         * The original name of the file (without a revision), e.g. `main.js`.
         */
        val originalName: String
        /**
         * The content type of the file, e.g. `text/javascript`.
         */
        val contentType: ContentType
        /**
         * The actual content of the file, necessary for sending responses.
         */
        val content: OutgoingContent.ReadChannelContent
    }
}

internal operator fun ReadableRevFileRegistry.plus(other: ReadableRevFileRegistry): ReadableRevFileRegistry = when (this) {
    EmptyRegistry, other -> other
    is CompositeRegistry -> also { add(other) }
    else -> CompositeRegistry(this, other)
}

internal data object EmptyRegistry : ReadableRevFileRegistry {
    override fun size(): Int = 0
    override fun get(path: String): RevisionedFile? = null
    override fun iterator(): Iterator<Nothing> = EmptyIterator

    internal object EmptyIterator : Iterator<Nothing> {
        override fun hasNext(): Boolean = false
        override fun next(): Nothing = throw NoSuchElementException()
    }
}

internal class CompositeRegistry(
    first: ReadableRevFileRegistry,
    second: ReadableRevFileRegistry,
) : ReadableRevFileRegistry {

    // lightweight collection
    private var registries = arrayOf(first, second)

    override fun size(): Int = registries.sumOf(ReadableRevFileRegistry::size)

    fun add(registry: ReadableRevFileRegistry) {
        if (registry != EmptyRegistry && registry !in registries) {
            registries += registry
        }
    }

    override fun get(path: String): RevisionedFile? {
        for (registry in registries) {
            val found = registry[path]
            if (found != null) return found
        }
        return null
    }

    override fun iterator(): Iterator<RevisionedFile> = registries.flatMap { it }.iterator()
    override fun toString(): String = "CompositeRegistry(size=${size()})"
}

/**
 * A registry for revisioned files, scoped under the given [path].
 *
 * ```kotlin
 * package com.example
 *
 * object AppAssets : RevFileRegistry("/assets/") {
 *     val main: RevisionedFile = resource("main.js")
 *     val styles: RevisionedFile = resource("styles.css")
 * }
 *
 * fun Application.module() {
 *     install(RevFilePlugin) {
 *         +AppAssets
 *     }
 * }
 * ```
 */
abstract class RevFileRegistry(path: String) : WriteableRevFileRegistry {
    private val path: String = '/' + path.trim('/') + '/'
    private val files = mutableMapOf<String, RevisionedFile>()

    final override fun size(): Int = files.size

    final override operator fun get(path: String): RevisionedFile? = files[path]

    final override fun register(entry: WriteableRevFileRegistry.Entry): RevisionedFile {
        require(entry.originalName.isNotBlank()) {
            "Cannot register a file with a blank name '${entry.originalName}': $entry"
        }
        val digest = entry.content.digestSha256()
        val name = determineName(entry.originalName, digest.revision())
        val revFile = RevisionedFile(
            contentType = entry.contentType.normalize(),
            path = path + name,
            content = entry.content,
            integrity = digest.integrity(),
            weakETag = digest.weakETag()
        )
        files[revFile.path] = revFile
        LOGGER.debug("Registered: ${revFile.path}")
        return revFile
    }

    final override fun iterator(): Iterator<RevisionedFile> = files.values.toTypedArray().iterator()
    override fun toString(): String = "RevFileRegistry(path='$path', size=${size()})"
}