package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.http.content.*
import java.io.File

private data class LocalFileEntry(
    override val originalName: String,
    override val contentType: ContentType,
    override val content: OutgoingContent.ReadChannelContent
) : WriteableRevFileRegistry.Entry


/**
 * Creates a new [RevisionedFile] from the given file and adds it to this [WriteableRevFileRegistry].
 */
fun WriteableRevFileRegistry.file(
    file: File,
    contentType: ContentType = ContentType.defaultForFile(file),
): RevisionedFile =
    LocalFileEntry(
        originalName = file.name,
        contentType = contentType,
        content = LocalFileContent(file, contentType),
    ).let(this::register)
