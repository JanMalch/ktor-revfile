plugins {
    `kotlin-dsl`
}

dependencies {
    // add plugins here, to use them in the plugins {} block
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.power.assert.plugin)
    implementation(libs.kotlinx.kover.plugin)
    implementation(libs.publish.plugin)
    implementation(libs.dokka.plugin)
}
