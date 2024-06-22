package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals


class RevFileTest {

    private object TestAssets : RevFileRegistry("/assets/") {
        val main = resource("main.js")
        // pretend we only chang the file content and not the name
        val main2 = resource("main2.js", name = "main.js")
        // force a content type
        val mainAsCss = resource("main3.js", contentType = ContentType.Text.CSS)
    }

    @Test
    fun `should register files from resources`() {
        assertEquals(3, TestAssets.size())
        assertEquals(ContentType.Text.JavaScript.withCharset(Charsets.UTF_8), TestAssets.main.contentType)
        unlessCi {
            assertEquals("/assets/main.aa4f186fdc.js", TestAssets.main.path)
            assertEquals("sha256-qk8Yb9znBHVgFUizgjNa2/+Ks6gXhzXHRBHhlF634Ls=", TestAssets.main.integrity)
        }
    }

    @Test
    fun `should revision based on the content`() {
        assertEquals(ContentType.Text.JavaScript.withCharset(Charsets.UTF_8), TestAssets.main2.contentType)
        unlessCi {
            assertEquals("/assets/main.0df0977963.js", TestAssets.main2.path)
            assertEquals("sha256-DfCXeWOEKkmemG4VS3+05dl+LxVOMTJfc0qxC0dRtWI=", TestAssets.main2.integrity)
        }
    }

    @Test
    fun `should accept any forced content type`() {
        assertEquals(ContentType.Text.CSS, TestAssets.mainAsCss.contentType)
        unlessCi {
            assertEquals("/assets/main3.59651a9751.js", TestAssets.mainAsCss.path)
        }
    }
}
