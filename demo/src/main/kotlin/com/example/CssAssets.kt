package com.example

import io.github.janmalch.ktor.revfile.RevFileRegistry
import io.github.janmalch.ktor.revfile.resource

object CssAssets : RevFileRegistry("assets/css") {
    val styles = resource("styles.css")
}