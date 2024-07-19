plugins {
    id("module.publication")
    id("kotlin-multiplatform-conventions")
}

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
