package io.github.janmalch.ktor.revfile

import io.github.janmalch.ktor.revfile.html.script
import io.github.janmalch.ktor.revfile.html.useSubresourceIntegrity
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.html.body
import kotlinx.html.head
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorPluginHtmlTest {

    private object TestAssets : RevFileRegistry("/assets/") {
        val main = resource("script.js")
    }

    @Test
    fun `should automatically respond with subresource integrity if so configured`() = testApplication {
        application {
            install(RevFilePlugin) {
                useSubresourceIntegrity = true
                +TestAssets
            }
            routing {
                get("/") {
                    call.respondHtml {
                        head {
                            script(TestAssets.main)
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
        val rev = "88488b27fa"
        val integrity = "sha256-iEiLJ/q/tYt8g+EXKOJHT33cllygjlQt/mdJxtdhIPU="

        assertTrue(response.contains(Regex("<script type=\"text/javascript\" src=\"/assets/script\\.[a-z0-9]+\\.js\" integrity=\"sha256-.+?\"></script>")))
        unlessCi {
            assertEquals(
                """
            <!DOCTYPE html>
            <html>
              <head>
                <script type="text/javascript" src="/assets/script.$rev.js" integrity="$integrity"></script>
              </head>
              <body>Hello, World!</body>
            </html>
            
        """.trimIndent(), response, ""
            ) // empty message for better output
        }
    }
}