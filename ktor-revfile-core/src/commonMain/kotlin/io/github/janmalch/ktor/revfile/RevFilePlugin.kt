package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlin.jvm.JvmName
import kotlin.time.Duration.Companion.days

/**
 * Configuration options for the [RevFilePlugin].
 */
data class RevFileConfig internal constructor(
    /**
     * The [CacheControl] header value to be applied to every response handled by this plugin.
     *
     * Default is `max-age=31536000, public, immutable`.
     */
    var cacheControl: String = CacheControl.MaxAge(
        maxAgeSeconds = 365.days.inWholeSeconds.toInt(),
        visibility = CacheControl.Visibility.Public,
    ).toString() + ", immutable",
    /**
     * Whether the plugin's call handler should handle [HttpHeaders.ETag] and [HttpHeaders.IfNoneMatch].
     *
     * Default is `true`.
     */
    var isETagEnabled: Boolean = true,
) {
    internal var registry: ReadableRevFileRegistry = EmptyRegistry
        private set

    @JvmName("add")
    operator fun ReadableRevFileRegistry.unaryPlus() {
        registry += this
    }
}

internal const val EMPTY_REGISTRY_ERROR = "No revisioned files registered after the RevFilePlugin installation phase. Did you forget to add a non-empty registry with the unaryPlus operator?"

/**
 * A Ktor server plugin for automatically revisioning files based on their content.
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
 *     // No further routing setup required.
 *     // For the final path use AppAssets.main.path throughout your app.
 *     install(RevFilePlugin) {
 *         +AppAssets
 *     }
 * }
 * ```
 *
 * @see RevFileConfig
 */
val RevFilePlugin = createApplicationPlugin(
    name = "RevFilePlugin",
    createConfiguration = ::RevFileConfig,
) {
    val registry = pluginConfig.registry
    check(registry.size() > 0) { EMPTY_REGISTRY_ERROR }
    val cacheControl = pluginConfig.cacheControl

    if (pluginConfig.isETagEnabled) {
        onCall { call ->
            val path = call.request.path()
            val revFile = registry[path] ?: return@onCall
            val eTag = revFile.weakETag

            call.response.header(HttpHeaders.CacheControl, cacheControl)
            call.response.header(HttpHeaders.ETag, eTag)

            val ifNoneMatch = call.request.header(HttpHeaders.IfNoneMatch)
            if (ifNoneMatch == eTag) {
                call.respond(HttpStatusCode.NotModified)
            } else {
                call.respond(revFile.content)
            }
        }
    } else {
        onCall { call ->
            val path = call.request.path()
            val revFile = registry[path] ?: return@onCall

            call.response.header(HttpHeaders.CacheControl, cacheControl)
            call.respond(revFile.content)
        }
    }
}
