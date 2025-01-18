package io.github.janmalch.ktor.revfile

/**
 * Read access for a revisioned file registry.
 *
 * @see RevFileRegistry
 */
interface ReadableRevFileRegistry : Iterable<RevisionedFile> {
    /** Returns the number of registered files. */
    fun size(): Int

    /**
     * Returns the revisioned file associated with the given path,
     * or `null` if no such file exists.
     */
    operator fun get(path: String): RevisionedFile?
}

internal operator fun ReadableRevFileRegistry.plus(other: ReadableRevFileRegistry): ReadableRevFileRegistry =
    when (this) {
        EmptyRegistry, other -> other
        is CompositeRegistry -> also { add(other) }
        else -> CompositeRegistry(this, other)
    }

internal data object EmptyRegistry : ReadableRevFileRegistry {
    override fun size(): Int = 0
    override fun get(path: String): RevisionedFile? = null
    override fun iterator(): Iterator<Nothing> = EmptyIterator

    internal object EmptyIterator : Iterator<Nothing> {
        override fun hasNext(): Boolean = false
        override fun next(): Nothing = throw NoSuchElementException()
    }
}

internal class CompositeRegistry(
    first: ReadableRevFileRegistry,
    second: ReadableRevFileRegistry,
) : ReadableRevFileRegistry {

    // lightweight collection
    private var registries = arrayOf(first, second)

    override fun size(): Int = registries.sumOf(ReadableRevFileRegistry::size)

    fun add(registry: ReadableRevFileRegistry) {
        if (registry != EmptyRegistry && registry !in registries) {
            registries += registry
        }
    }

    override fun get(path: String): RevisionedFile? {
        for (registry in registries) {
            val found = registry[path]
            if (found != null) return found
        }
        return null
    }

    override fun iterator(): Iterator<RevisionedFile> = registries.flatMap { it }.iterator()
    override fun toString(): String = "CompositeRegistry(size=${size()})"
}
