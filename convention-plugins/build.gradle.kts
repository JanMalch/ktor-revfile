plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.powerAssertPlugin)
    implementation(libs.kotlinx.koverPlugin)
    implementation(libs.nexus.publish)
}
