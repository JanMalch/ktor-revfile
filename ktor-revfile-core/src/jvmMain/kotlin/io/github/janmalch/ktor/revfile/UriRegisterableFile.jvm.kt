package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import io.ktor.http.content.*
import java.net.URI

private data class UriEntry(
    override val originalName: String,
    override val contentType: ContentType,
    override val content: OutgoingContent.ReadChannelContent,
) : WriteableRevFileRegistry.Entry

/**
 * Creates a new [RevisionedFile] from the given URI and adds it to this [WriteableRevFileRegistry].
 */
fun WriteableRevFileRegistry.uri(
    uri: URI,
    name: String = uri.toString().substringAfterLast('/'),
    contentType: ContentType = ContentType.defaultForFilePath(uri.path),
): RevisionedFile = UriEntry(
    originalName = name,
    contentType = contentType,
    content = URIFileContent(uri, contentType),
).let(this::register)

/**
 * Creates a new [RevisionedFile] from the given JVM resource and adds it to this [WriteableRevFileRegistry].
 *
 * **Example:**
 *
 * Assuming the following folder structure:
 *
 * ```
 * src
 * └── main
 *     ├── kotlin
 *     │   └── com
 *     │       └── example
 *     │           └── AppAssets.kt
 *     └── resources
 *         └── main.js
 * ```
 *
 * You can create a revisioned file for `main.js`:
 *
 * ```kotlin
 * package com.example
 *
 * object AppAssets : RevFileRegistry("/assets/") {
 *     val main = resource("main.js")
 * }
 * ```
 *
 * @param resource the name of the resource to load
 * @param name the name to be used for the file
 * @param contentType optionally force a content type, will be inferred otherwise
 * @param classLoader the `ClassLoader` to use for loading the resource
 * @see ClassLoader.getResource
 * @throws IllegalArgumentException if the resource cannot be loaded.
 */
fun WriteableRevFileRegistry.resource(
    resource: String,
    name: String = resource.substringAfterLast('/'),
    contentType: ContentType? = null,
    // TODO: see if this class loader makes any sense
    classLoader: ClassLoader = checkNotNull(this::class.java.classLoader) {
        "Failed to get a class loader for loading resource '$resource'."
    }
): RevisionedFile {
    val uri = requireNotNull(classLoader.getResource(resource)?.toURI()) {
        "Unable to find a resource for '$resource'."
    }
    return this.uri(
        uri = uri,
        name = name,
        contentType = contentType ?: ContentType.defaultForFilePath(uri.path)
    )
}
