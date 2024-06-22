# Module ktor-revfile-core

A [Ktor server plugin](https://ktor.io/docs/server-plugins.html) made for automatically revisioning static files.

A revisioned file will include a short hash of its content in the file name.
This enables optimal caching without you ever having to change the name of the local file. 

## Usage

```kotlin
package com.example

object AppAssets : RevFileRegistry("/assets/") {
    val main: RevisionedFile = resource("main.js")
    val styles: RevisionedFile = resource("styles.css")
}

fun Application.module() {
    install(RevFilePlugin) {
        // Optional configuration; displayed values are default
        cacheControl = "max-age=31536000, public, immutable"
        isETagEnabled = true
        // Add your registries to the plugin
        +AppAssets
    }
}
```

You can then fetch the `main.js` file via a generated path, e.g. `/assets/main.5c7ca8845f.js`.
The revision infix depends entirely on the file's content.

Now if you change the content of the `main.js` file, the URL will also change, e.g. `/assets/main.0729a30ef1.js`.

The plugin will log the registered paths on the `DEBUG` level.
In your code, you can access it via the `path` property on `RevisionedFile`.
