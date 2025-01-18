plugins {
    id("conventions.kmp")
    id("conventions.dokka")
    id("conventions.publish")
}

description = "A Ktor server plugin made for automatically revisioning static files."

dependencies {
    commonMainImplementation(libs.okio) // used for streaming hashes
    commonMainImplementation(libs.kotlinx.io)
    commonMainImplementation(libs.ktor.server.core)
    commonMainImplementation(libs.ktor.utils)

    commonTestImplementation(libs.ktor.server.test.host)
    commonTestImplementation(libs.ktor.server.html.builder)
    commonTestImplementation(libs.kotlinx.html)
    commonTestImplementation(libs.slf4j.nop)
}
