plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.power.assert) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.publish) apply false
    alias(libs.plugins.bcv)
    alias(libs.plugins.dokka)
}

// ./gradlew dokkaGenerate to generate docs for entire project
dokka {
    moduleName.set("ktor-revfile")
}

// https://kotlinlang.org/docs/dokka-migration.html#update-documentation-aggregation-in-multi-module-projects
dependencies {
    dokka(project(":ktor-revfile-core"))
    dokka(project(":ktor-revfile-html"))
}
