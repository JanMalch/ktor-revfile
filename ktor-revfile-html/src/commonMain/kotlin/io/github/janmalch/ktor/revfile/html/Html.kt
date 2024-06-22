package io.github.janmalch.ktor.revfile.html

import io.github.janmalch.ktor.revfile.RevFileConfig
import io.github.janmalch.ktor.revfile.RevisionedFile
import io.github.janmalch.ktor.revfile.RevFilePlugin
import kotlinx.html.script
import kotlinx.html.link
import kotlinx.html.HtmlTagMarker
import kotlinx.html.FlowOrMetaDataOrPhrasingContent
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.SCRIPT
import kotlinx.html.LINK
import kotlinx.html.HEAD

/**
 * Global configuration for HTML extension functions.
 *
 * Configure in the [RevFilePlugin] installation phase.
 */
object RevFileHtml {
    /**
     * Determines whether the subresource `integrity` attribute should be set by default,
     * when calling the convenience [script] and [stylesheet] functions.
     *
     * Read more on [subresource integrity](https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity).
     *
     * @see RevFileConfig.useSubresourceIntegrity
     */
    var useSubresourceIntegrity: Boolean = false
        internal set
}

/**
 * Determines whether the subresource `integrity` attribute should be set by default,
 * when calling the convenience [script] and [stylesheet] functions.
 *
 * Read more on [subresource integrity](https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity).
 */
@Suppress("UnusedReceiverParameter")
var RevFileConfig.useSubresourceIntegrity: Boolean
    get() = RevFileHtml.useSubresourceIntegrity
    set(value) {
        RevFileHtml.useSubresourceIntegrity = value
    }

/**
 * Adds a `<script>` tag for the given revisioned file.
 *
 * @param file the revisioned file
 * @param crossorigin value for the `crossorigin` attribute. Default is `null`.
 * @param integrity whether the `integrity` attribute should be set. Default can be configured with [useSubresourceIntegrity] while installing the [RevFile.Plugin].
 * @param block optional block to further customize the `<script>` tag
 */
@HtmlTagMarker
fun FlowOrMetaDataOrPhrasingContent.script(
    file: RevisionedFile,
    crossorigin: ScriptCrossorigin? = null,
    integrity: Boolean = RevFileHtml.useSubresourceIntegrity,
    block: SCRIPT.() -> Unit = {},
) {
    script(
        src = file.path,
        type = "${file.contentType.contentType}/${file.contentType.contentSubtype}",
        crossorigin = crossorigin
    ) {
        if (integrity) {
            this.integrity = file.integrity
        }
        block()
    }
}

/**
 * Adds a `<link rel="stylesheet">` tag for the given revisioned file.
 *
 * @param file the revisioned file
 * @param crossorigin value for the `crossorigin` attribute. Default is `null`.
 * @param integrity whether the `integrity` attribute should be set. Default can be configured with [useSubresourceIntegrity] while installing the [RevFile.Plugin].
 * @param block optional block to further customize the `<link>` tag
 */
@HtmlTagMarker
fun HEAD.stylesheet(
    file: RevisionedFile,
    crossorigin: ScriptCrossorigin? = null,
    integrity: Boolean = RevFileHtml.useSubresourceIntegrity,
    block: LINK.() -> Unit = {},
) {
    link(
        href = file.path,
        type = "${file.contentType.contentType}/${file.contentType.contentSubtype}",
        rel = "stylesheet"
    ) {
        if (crossorigin != null) {
            attributes["crossorigin"] = crossorigin.realValue
        }
        if (integrity) {
            this.integrity = file.integrity
        }
        block()
    }
}

