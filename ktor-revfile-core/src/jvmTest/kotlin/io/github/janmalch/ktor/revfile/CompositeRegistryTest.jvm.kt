package io.github.janmalch.ktor.revfile

import kotlin.test.Test
import kotlin.test.assertEquals

class CompositeRegistryTest {

    private object ExampleRegistry : RevFileRegistry("/example/") {
        val main = resource("main.js")
        val main2 = resource("main2.js")
    }

    private object TestRegistry : RevFileRegistry("/test/") {
        val main2 = resource("main2.js")
        val main3 = resource("main3.js")
    }

    @Test
    fun `should not add duplicates`() {
        val composite = CompositeRegistry(ExampleRegistry, TestRegistry)
        assertEquals(4, composite.size())
        composite.add(TestRegistry)
        assertEquals(4, composite.size())
    }

    @Test
    fun `should iterate correctly`() {
        val composite = CompositeRegistry(ExampleRegistry, TestRegistry)
        assertEquals(
            listOf(
                ExampleRegistry.main,
                ExampleRegistry.main2,
                TestRegistry.main2,
                TestRegistry.main3,
            ),
            composite.toList()
        )
    }
}
