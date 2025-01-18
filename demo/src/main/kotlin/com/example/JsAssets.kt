package com.example

import io.github.janmalch.ktor.revfile.RevFileRegistry
import io.github.janmalch.ktor.revfile.resource

object JsAssets : RevFileRegistry("assets/js") {
    val main = resource("main.js")
    val messages = resource("messages.js")
}