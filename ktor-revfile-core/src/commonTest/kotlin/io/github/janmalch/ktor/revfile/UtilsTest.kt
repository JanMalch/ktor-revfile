package io.github.janmalch.ktor.revfile

import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun `determineName should work simple file names`() {
        assertEquals("demo.rev.js", determineName("demo.js", "rev"))
    }

    @Test
    fun `determineName should work for file names without extension`() {
        assertEquals("demo.rev", determineName("demo", "rev"))
    }

    @Test
    fun `determineName should work for file names with multiple dots`() {
        assertEquals("demo.v2.0.0.rev.js", determineName("demo.v2.0.0.js", "rev"))
    }

    @Test
    fun `ContentType_normalize should do nothing if not necessary`() {
        val type = ContentType.Text.CSS
        assertEquals(type, type.normalize())
        val parameterizedType = ContentType.Text.CSS.withCharset(Charsets.UTF_8)
        assertEquals(parameterizedType, parameterizedType.normalize())
    }

    @Test
    fun `ContentType_normalize should replace application_javascript with text_javascript and retain parameters`() {
        val expected = ContentType.Text.JavaScript.withCharset(Charsets.UTF_8)
        val actual = ContentType.Application.JavaScript.withCharset(Charsets.UTF_8).normalize()
        assertEquals(expected, actual)
    }
}