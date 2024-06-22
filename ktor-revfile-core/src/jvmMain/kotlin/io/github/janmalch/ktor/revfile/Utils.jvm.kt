package io.github.janmalch.ktor.revfile

import io.ktor.http.content.*
import io.ktor.utils.io.jvm.javaio.*
import okio.*
import okio.HashingSink.Companion.sha256

internal actual fun OutgoingContent.ReadChannelContent.digestSha256(): ByteString =
    sha256(blackholeSink()).use { hashingSink ->
        readFrom().toInputStream().source().buffer().use { source ->
            source.readAll(hashingSink)
            hashingSink.hash
        }
    }
