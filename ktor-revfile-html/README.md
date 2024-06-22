# Module ktor-revfile-html

Tiny extensions for [kotlinx.html](https://github.com/Kotlin/kotlinx.html), 
providing direct interop with the main `ktor-revfile-core` library.

## Usage

Currently, two functions are available: `script` and `stylesheet`.

```kotlin
call.respondHtml {
    head {
        // with this library
        script(AppAssets.main)
        stylesheet(AppAssets.styles)
        
        // without this library
        script(src = AppAssets.main.path) {}
        link(href = AppAssets.styles.path, rel = "stylesheet")
    }
}
```

Note that the content type won't be verified.

If you feel the need, you can enable out-of-the-box [subresource integrity](https://developer.mozilla.org/en-US/docs/Web/Security/Subresource_Integrity).

```kotlin
install(RevFilePlugin) {
    useSubresourceIntegrity = true // false by default
}
```
