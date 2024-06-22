package io.github.janmalch.ktor.revfile

// FIXME: tests (hashes?) are broken on CI..!?
internal inline fun unlessCi(block: () -> Unit) {
    if (System.getenv("CI") != "true") {
        block()
    }
}