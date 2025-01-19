package io.github.janmalch.ktor.revfile

import io.ktor.http.ContentType
import io.ktor.http.defaultForFile
import io.ktor.server.http.content.LocalFileContent
import java.io.File


/**
 * Creates a new [RevisionedFile] from the given file and adds it to this [WriteableRevFileRegistry].
 */
fun WriteableRevFileRegistry.file(
    file: File,
    contentType: ContentType = ContentType.defaultForFile(file),
    name: String = file.name,
): RevisionedFile =
    register(
        originalName = name,
        contentType = contentType,
        content = LocalFileContent(file, contentType),
    )
