package io.github.janmalch.ktor.revfile

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.defaultForFilePath
import io.ktor.utils.io.ByteReadChannel
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

private class SourceOutgoingContent(
    private val content: () -> Source,
) : OutgoingContent.ReadChannelContent() {
    // based on io.ktor.server.http.content.transformDefaultContent
    override fun readFrom(): ByteReadChannel = ByteReadChannel(content())
}

/**
 * Creates a new [RevisionedFile] from the given source and adds it to this [WriteableRevFileRegistry].
 */
fun WriteableRevFileRegistry.source(
    originalName: String,
    contentType: ContentType = ContentType.defaultForFilePath(originalName),
    content: () -> Source,
): RevisionedFile {
    val outgoing = SourceOutgoingContent(content)
    return register(originalName, contentType, outgoing)
}

/**
 * Creates a new [RevisionedFile] from the given source and adds it to this [WriteableRevFileRegistry].
 */
fun WriteableRevFileRegistry.source(
    path: Path,
    contentType: ContentType = ContentType.defaultForFilePath(path.toString()),
    originalName: String = path.name,
    fileSystem: FileSystem = SystemFileSystem,
): RevisionedFile {
    val outgoing = SourceOutgoingContent { fileSystem.source(path).buffered() }
    return register(originalName, contentType, outgoing)
}

