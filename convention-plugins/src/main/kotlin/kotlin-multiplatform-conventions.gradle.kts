import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.powerassert.gradle.PowerAssertGradleExtension

plugins {
    kotlin("multiplatform")
    kotlin("plugin.power-assert")
    id("org.jetbrains.kotlinx.kover")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()
    // https://github.com/Kotlin/kotlinx.coroutines/blob/1bffe67a32d9d0285320f5b23fa94bc2b5f2b92e/buildSrc/src/main/kotlin/kotlin-multiplatform-conventions.gradle.kts#L26
    // According to https://kotlinlang.org/docs/native-target-support.html
    // Tier 1
    linuxX64()
    macosX64()
    macosArm64()
    // Tier 2
    linuxArm64()
    // Tier 3
    // mingwX64() // TODO: not supported by Ktor Native


    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    sourceSets {
        val commonMain by getting {
            dependencies {
                // put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.findLibrary("kotlin.test").get())
            }
        }
    }

    tasks.withType<Test>().configureEach {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            showCauses = true
            showExceptions = true
            showStackTraces = false
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    extensions.getByType(PowerAssertGradleExtension::class).apply {
        functions.set(
            listOf(
                "kotlin.assert",
                "kotlin.test.assertTrue",
                "kotlin.test.assertEquals",
                "kotlin.test.assertNull"
            )
        )
    }
}
