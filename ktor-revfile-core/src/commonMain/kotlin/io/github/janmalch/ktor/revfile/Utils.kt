package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import io.ktor.http.content.*
import okio.ByteString

/**
 * Creates a name from the given original name and file revision.
 */
internal fun determineName(originalName: String, revision: String): String =
    when (val idx = originalName.lastIndexOf('.')) {
        -1 -> "${originalName}.$revision"
        else -> originalName.substring(0, idx) + '.' + revision + originalName.substring(idx)
    }

/**
 * Formats this ByteString to a string to be used as the revision part.
 */
internal fun ByteString.revision(): String {
    return hex().take(10)
}

/**
 * Formats this ByteString to a weak ETag.
 */
internal fun ByteString.weakETag(): String {
    return "W/\"${base64()}\""
}

/**
 * Formats this ByteString for the `integrity` HTML attribute.
 */
internal fun ByteString.integrity(): String {
    return "sha256-${base64()}"
}

/**
 * Normalizes this content types, like replacing `application/javascript` with `text/javascript`,
 * retaining any parameters.
 */
internal fun ContentType.normalize(): ContentType = when {
    match(ContentType.Application.JavaScript) -> ContentType(
        contentType = ContentType.Text.JavaScript.contentType,
        contentSubtype = ContentType.Text.JavaScript.contentSubtype,
        parameters = parameters,
    )

    else -> this
}

/**
 * Generates a ByteString which is the SHA256 value of this outgoing content.
 */
internal expect fun OutgoingContent.ReadChannelContent.digestSha256(): ByteString
