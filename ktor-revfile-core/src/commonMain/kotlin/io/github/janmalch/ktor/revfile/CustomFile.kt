package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import io.ktor.http.content.*

private data class CustomEntry(
    override val originalName: String,
    override val contentType: ContentType,
    override val content: OutgoingContent.ReadChannelContent,
) : WriteableRevFileRegistry.Entry

/**
 * Creates a new custom [RevisionedFile] and adds it to this [WriteableRevFileRegistry].
 *
 * This is the most general function to create a revisioned file.
 */
fun WriteableRevFileRegistry.create(
    originalName: String,
    contentType: ContentType = ContentType.defaultForFilePath(originalName),
    content: () -> OutgoingContent.ReadChannelContent,
): RevisionedFile {
    return CustomEntry(originalName, contentType, content()).let(this::register)
}

