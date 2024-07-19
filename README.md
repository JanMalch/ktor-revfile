# Ktor RevFile

A [Ktor server plugin](https://ktor.io/docs/server-plugins.html) made for automatically revisioning static files.

A revisioned file will include a short hash of its content in the file name.
This enables optimal caching without you ever having to change the name of the local file.

## Usage

```kotlin
package com.example

object AppAssets : RevFileRegistry("/assets/") {
    val main = resource("main.js")
    val styles = resource("styles.css")
}

fun Application.module() {
    install(RevFilePlugin) {
        +AppAssets
    }
}
```

Checkout the [complete documentation](https://janmalch.github.io/ktor-revfile) for usage details.
