package io.github.janmalch.ktor.revfile

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent

/**
 * Write access for a revisioned file registry.
 *
 * @see RevFileRegistry
 */
interface WriteableRevFileRegistry {
    /**
     * Registers and returns a revisioned file.
     * This is the most general function to create a revisioned file.
     *
     * @param originalName the original name of the file (without a revision), e.g. `main.js`.
     * @param contentType the content type of the file, e.g. `text/javascript`.
     * @param content the actual content of the file, necessary for sending responses.
     */
    fun register(
        originalName: String,
        contentType: ContentType,
        content: OutgoingContent.ReadChannelContent
    ): RevisionedFile
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
abstract class RevFileRegistry(path: String) : WriteableRevFileRegistry, ReadableRevFileRegistry {
    /**
     * The base path for this registry.
     */
    val basePath: String = '/' + path.trim('/') + '/'
    private val files = mutableMapOf<String, RevisionedFile>()

    final override fun size(): Int = files.size

    final override operator fun get(path: String): RevisionedFile? = files[path]

    final override fun register(
        originalName: String,
        contentType: ContentType,
        content: OutgoingContent.ReadChannelContent
    ): RevisionedFile {
        require(originalName.isNotBlank()) {
            "Cannot register a file with a blank name: '${originalName}'"
        }
        val digest = content.digestSha256()
        val name = determineName(originalName, digest.revision())
        val revFile = RevisionedFile(
            contentType = contentType.normalize(),
            path = basePath + name,
            originalName = originalName,
            content = content,
            integrity = digest.integrity(),
            weakETag = digest.weakETag()
        )
        files[revFile.path] = revFile
        LOGGER.debug("Registered: ${revFile.path}")
        return revFile
    }

    final override fun iterator(): Iterator<RevisionedFile> = files.values.toTypedArray().iterator()
    override fun toString(): String = "RevFileRegistry(basePath='$basePath', size=${size()})"
}
