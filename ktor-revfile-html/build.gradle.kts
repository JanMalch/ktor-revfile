plugins {
    id("module.publication")
    id("kotlin-multiplatform-conventions")
}

dependencies {
    commonMainImplementation(project(":ktor-revfile-core"))
    commonMainImplementation(libs.kotlinx.html)
    commonMainImplementation(libs.ktor.server.core) // for ContentType

    commonTestImplementation(libs.ktor.server.test.host)
    commonTestImplementation(libs.ktor.server.html.builder)
    commonTestImplementation(libs.kotlinx.html)
    commonTestImplementation(libs.slf4j.nop)
}
