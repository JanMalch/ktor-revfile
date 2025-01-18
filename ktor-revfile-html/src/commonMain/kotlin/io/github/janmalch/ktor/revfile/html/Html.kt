package io.github.janmalch.ktor.revfile.html

import io.github.janmalch.ktor.revfile.RevFileConfig
import io.github.janmalch.ktor.revfile.RevFilePlugin
import io.github.janmalch.ktor.revfile.RevisionedFile
import kotlinx.html.FlowOrMetaDataOrPhrasingContent
import kotlinx.html.HEAD
import kotlinx.html.HtmlTagMarker
import kotlinx.html.LINK
import kotlinx.html.SCRIPT
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.emptyMap
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.unsafe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

    /**
     * Determines whether the [script] function should set the `type` to `module` by default.
     * You need modules if you are using import statements and import maps.
     *
     * @see RevFileConfig.useJsModules
     */
    var useJsModules: Boolean = false
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
 * Determines whether the [script] function should set the `type` to `module` by default.
 * You need modules if you are using import statements and import maps.
 */
@Suppress("UnusedReceiverParameter")
var RevFileConfig.useJsModules: Boolean
    get() = RevFileHtml.useJsModules
    set(value) {
        RevFileHtml.useJsModules = value
    }

/**
 * Adds a `<script>` tag for the given revisioned file.
 *
 * @param file the revisioned file
 * @param crossorigin value for the `crossorigin` attribute. Default is `null`.
 * @param integrity whether the `integrity` attribute should be set. Default can be configured with [useSubresourceIntegrity] while installing the [RevFilePlugin].
 * @param module whether the `type` attribute should be set to `module`. Default can be configured with [useJsModules] while installing the [RevFilePlugin].
 * @param block optional block to further customize the `<script>` tag
 */
@HtmlTagMarker
fun FlowOrMetaDataOrPhrasingContent.script(
    file: RevisionedFile,
    crossorigin: ScriptCrossorigin? = null,
    integrity: Boolean = RevFileHtml.useSubresourceIntegrity,
    module: Boolean = RevFileHtml.useJsModules,
    block: SCRIPT.() -> Unit = {},
) {
    script(
        src = file.path,
        type = "${file.contentType.contentType}/${file.contentType.contentSubtype}",
        crossorigin = crossorigin
    ) {
        if (module) {
            type = "module"
        }
        if (integrity) {
            this.integrity = file.integrity
        }
        block()
    }
}

/**
 * An `importmap` definition.
 * @see importMap
 */
@Serializable
data class ImportMap(
    @SerialName("imports")
    val imports: Map<String, String> = emptyMap,
    @SerialName("scopes")
    val scopes: Map<String, Map<String, String>> = emptyMap(),
    @SerialName("integrity")
    val integrity: Map<String, String> = emptyMap,
) {
    constructor(files: Array<out RevisionedFile>, integrity: Boolean) : this(
        imports = files.associate { it.originalName to it.path },
        integrity = if (integrity) files.associate { it.path to it.integrity } else emptyMap
    )
}

/**
 * Adds a `<script type="importmap">` tag for the given [ImportMap].
 *
 * Read more on [import maps](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script/type/importmap).
 *
 * @param importMap the script's content
 */
@HtmlTagMarker
fun FlowOrMetaDataOrPhrasingContent.importMap(importMap: ImportMap) {
    script(
        type = "importmap",
    ) {
        unsafe { +Json.encodeToString(importMap) }
    }
}

/**
 * Adds a `<script type="importmap">` tag for the given revisioned [files].
 *
 * Read more on [import maps](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script/type/importmap).
 *
 * @param files revisioned files to include in the import map
 * @param integrity whether to generate the integrity metadata map or not
 */
@HtmlTagMarker
fun FlowOrMetaDataOrPhrasingContent.importMap(
    vararg files: RevisionedFile,
    integrity: Boolean = RevFileHtml.useSubresourceIntegrity,
) {
    importMap(ImportMap(files, integrity))
}

/**
 * Adds a `<link rel="stylesheet">` tag for the given revisioned file.
 *
 * @param file the revisioned file
 * @param crossorigin value for the `crossorigin` attribute. Default is `null`.
 * @param integrity whether the `integrity` attribute should be set. Default can be configured with [useSubresourceIntegrity] while installing the [RevFilePlugin].
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

