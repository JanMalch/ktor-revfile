plugins {
    `maven-publish`
    signing
}

publishing {
    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(tasks.register("${name}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveAppendix.set(this@withType.name)
        })

        // Provide artifacts information required by Maven Central
        pom {
            name.set("Ktor RevFile")
            description.set("A Ktor server plugin made for automatically revisioning static files.")
            url.set("https://janmalch.github.io/ktor-revfile")

            licenses {
                license {
                    name.set("Apache-2.0")
                    url.set("https://opensource.org/license/apache-2-0")
                }
            }
            developers {
                developer {
                    id.set("JanMalch")
                    name.set("JanMalch")
                }
            }
            scm {
                url.set("https://github.com/JanMalch/ktor-revfile")
            }
        }
    }
}

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}
