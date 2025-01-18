import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    id("org.jetbrains.dokka")
}

dokka {
    dokkaSourceSets.configureEach {
        includes.from("README.md")
        documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)

        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl("https://github.com/janmalch/ktor-revfile/tree/main/${projectDir.name}/src")
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLinks.register("ktor") {
            url("https://api.ktor.io/")
        }
    }
}
