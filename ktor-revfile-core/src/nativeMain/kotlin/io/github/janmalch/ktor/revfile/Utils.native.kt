package io.github.janmalch.ktor.revfile

import io.ktor.http.content.*
import okio.ByteString
import okio.HashingSink.Companion.sha256
import okio.blackholeSink
import okio.use

internal actual fun OutgoingContent.ReadChannelContent.digestSha256(): ByteString =
    sha256(blackholeSink()).use { hashingSink ->
        TODO() // FIXME
    }
