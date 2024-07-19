package io.github.janmalch.ktor.revfile

import io.ktor.http.content.OutgoingContent
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.runBlocking
import okio.ByteString
import okio.HashingSink.Companion.sha256
import okio.blackholeSink
import okio.buffer
import okio.use

internal actual fun OutgoingContent.ReadChannelContent.digestSha256(): ByteString =
    sha256(blackholeSink()).use { hashingSink ->
        hashingSink.buffer().use { sink ->
            val channel = readFrom()
            do {
                val byteArray = ByteArray(1024)
                val currentRead = runBlocking { channel.readAvailable(byteArray)  }
                sink.write(byteArray)
            } while (currentRead > 0)
        }
        hashingSink.hash
    }

