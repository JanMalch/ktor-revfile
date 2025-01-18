package com.example

import io.github.janmalch.ktor.revfile.RevFilePlugin
import io.github.janmalch.ktor.revfile.html.importMap
import io.github.janmalch.ktor.revfile.html.script
import io.github.janmalch.ktor.revfile.html.stylesheet
import io.github.janmalch.ktor.revfile.html.useJsModules
import io.github.janmalch.ktor.revfile.html.useSubresourceIntegrity
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.p

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(RevFilePlugin) {
        useSubresourceIntegrity = true
        useJsModules = true // necessary for import maps
        +JsAssets
        +CssAssets
    }

    install(CachingHeaders) {
        options { call, outgoingContent ->
            if (HttpHeaders.CacheControl in outgoingContent.headers) return@options null
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.JavaScript, ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }

    routing {
        get("/") {
            call.respondHtml {
                head {
                    stylesheet(CssAssets.styles)
                    importMap(JsAssets.messages)
                    // main directly imports messages.js
                    script(JsAssets.main)
                }
                body {
                    h1 { +"Rev-File Demo" }
                    p {
                        id = "message"
                        +"Import map? ..."
                    }
                }
            }
        }
    }
}
