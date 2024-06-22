package io.github.janmalch.ktor.revfile

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RevFileRegistryTest {

    private object ExampleRegistry : RevFileRegistry("/example/")
    private object TestRegistry : RevFileRegistry("/test/")
    private object DemoRegistry : RevFileRegistry("/demo/")

    @Test
    fun `plus should create optimal registries`() {
        assertEquals(EmptyRegistry, EmptyRegistry + EmptyRegistry)
        assertEquals(ExampleRegistry, EmptyRegistry + ExampleRegistry)
        assertEquals(ExampleRegistry, ExampleRegistry + ExampleRegistry)

        val composite = ExampleRegistry + TestRegistry
        assertTrue(composite is CompositeRegistry)
        assertTrue((composite + DemoRegistry) is CompositeRegistry)
    }
}
