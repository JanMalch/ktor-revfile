plugins {
    id("conventions.kmp")
    id("conventions.dokka")
    id("conventions.publish")
}

description = "Tiny extensions for kotlinx.html."

dependencies {
    commonMainImplementation(project(":ktor-revfile-core"))
    commonMainImplementation(libs.kotlinx.html)
    commonMainImplementation(libs.ktor.http) // for ContentType

    commonTestImplementation(libs.ktor.server.test.host)
    commonTestImplementation(libs.ktor.server.html.builder)
    commonTestImplementation(libs.kotlinx.html)
    commonTestImplementation(libs.slf4j.nop)
}
