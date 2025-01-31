package io.github.janmalch.ktor.revfile

import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent

/**
 * Describes a revisioned file, meaning its [path] is unique based on its content.
 *
 * Revisioned files must be created by using one of the various extension functions on [WriteableRevFileRegistry].
 * [create] is the most general function to create a revisioned file,
 * while [source] provides interop with `kotlinx.io`.
 *
 * Depending on your platform, convenient factories like [resource] or [file] are also available.
 *
 * ```kotlin
 * package com.example
 *
 * object AppAssets : RevFileRegistry("/assets/") {
 *     val main: RevisionedFile = resource("main.js")
 *     val styles: RevisionedFile = resource("styles.css")
 * }
 */
class RevisionedFile internal constructor(
    /**
     * The content type of this file, e.g. `text/javascript`
     *
     * Can be inferred or specified manually.
     */
    val contentType: ContentType,
    /**
     * The full path of this file, e.g. `/assets/example.5c7ca8845f.js`
     */
    val path: String,
    /**
     * The original name of the file (without a revision), e.g. `main.js`.
     */
    val originalName: String,
    /**
     * The generated weak ETag for this file, e.g. `W/"XHyohF/kGOwoCv4RWnG0Epk/njHhmJdiOntPLZiMhQE="`
     */
    internal val weakETag: String,
    /**
     * The computed integrity for this file, e.g. `sha256-XHyohF/kGOwoCv4RWnG0Epk/njHhmJdiOntPLZiMhQE=`.
     */
    val integrity: String,
    /**
     * The content for this file, necessary for handling responses.
     */
    internal val content: OutgoingContent.ReadChannelContent,
) {
    init {
        require(weakETag.startsWith("W/\"") && weakETag.endsWith('"') && weakETag.length > 4) {
            "Provided weak ETag is invalid: $weakETag"
        }
        require(integrity.startsWith("sha256-") && integrity.length > 7) {
            "Provided subresource integrity is invalid: $integrity"
        }
    }

    override fun toString(): String {
        return "RevisionedFile(path='$path', contentType=$contentType, originalName='$originalName', integrity='$integrity')"
    }
}
