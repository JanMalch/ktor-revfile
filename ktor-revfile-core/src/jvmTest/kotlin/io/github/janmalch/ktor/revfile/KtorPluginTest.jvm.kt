package io.github.janmalch.ktor.revfile

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.script
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail


class KtorPluginTest {

    private object TestAssets : RevFileRegistry("/assets/") {
        val main = resource("main.js")
    }

    @Test
    fun `should throw if registry is empty`() = testApplication {
        application {
            install(RevFilePlugin)

            routing {
                get("/") {
                    call.respondHtml {
                        head {
                            script(src = TestAssets.main.path) {}
                        }
                        body {
                            +"Hello, World!"
                        }
                    }
                }
            }
        }
        val client = createClient { }
        try {
            val response = client.get("/")
            fail("Making a request should fail, but got a status ${response.status.value} response")
        } catch (e: IllegalStateException) {
            assertEquals(EMPTY_REGISTRY_ERROR, e.message)
        }
    }

    @Test
    fun `should automatically respond with registered files`() = testApplication {
        application {
            install(RevFilePlugin) {
                +TestAssets
            }

            routing {
                get("/") {
                    call.respondHtml {
                        head {
                            script(src = TestAssets.main.path) {}
                        }
                        body {
                            +"Hello, World!"
                        }
                    }
                }
            }
        }

        val client = createClient { }
        val response = client.get("/").bodyAsText()

        assertTrue(response.contains(Regex("<script src=\"/assets/main\\.[a-z0-9]+?\\.js\"></script>")))
        unlessCi {
            assertEquals(
                """
            <!DOCTYPE html>
            <html>
              <head>
                <script src="/assets/main.aa4f186fdc.js"></script>
              </head>
              <body>Hello, World!</body>
            </html>
            
        """.trimIndent(), response, ""
            ) // empty message for better output

        }
    }

    @Test
    fun `should allow for configuration of the cache control header`() = testApplication {
        application {
            install(RevFilePlugin) {
                cacheControl = "private, no-store, max-age=0"
                +TestAssets
            }
        }

        val client = createClient { }
        val response = client.get("/assets/main.aa4f186fdc.js")

        unlessCi {
            assertEquals("text/javascript; charset=UTF-8", response.headers[HttpHeaders.ContentType])
            assertEquals("private, no-store, max-age=0", response.headers[HttpHeaders.CacheControl])
            assertEquals("W/\"qk8Yb9znBHVgFUizgjNa2/+Ks6gXhzXHRBHhlF634Ls=\"", response.headers[HttpHeaders.ETag])
        }
    }
}