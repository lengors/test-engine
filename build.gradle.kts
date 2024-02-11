import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.versioning.VersioningConfiguration
import org.jetbrains.dokka.versioning.VersioningPlugin

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.kotlinx.kover")
    `maven-publish`
    signing
}

group = "io.github.lengors"

repositories {
    mavenCentral()
}

dependencies {
    val dokkaVersion = project.properties["dokkaVersion"]
    dokkaHtmlPlugin("org.jetbrains.dokka:versioning-plugin:$dokkaVersion")
}

buildscript {
    dependencies {
        val dokkaVersion = project.properties["dokkaVersion"]
        classpath("org.jetbrains.dokka:dokka-base:$dokkaVersion")
        classpath("org.jetbrains.dokka:versioning-plugin:$dokkaVersion")
    }
}

kotlin {
    jvm {
        jvmToolchain(21)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    js {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled = true
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting
        val jvmTest by getting

        val jsMain by getting
        val jsTest by getting
    }
}

tasks.koverBinaryReport {
    dependsOn(tasks.named("allTests"))
}

tasks.koverHtmlReport {
    dependsOn(tasks.named("allTests"))
}

tasks.koverXmlReport {
    dependsOn(tasks.named("allTests"))
}


tasks.dokkaHtml {
    val docsDir = projectDir
        .resolve("build")
        .resolve("dokka")
        .resolve("generated")
    val docVersionsDir = docsDir.resolve("version")
    val currentVersion = project.version.toString()
    val currentDocsDir = docVersionsDir.resolve(currentVersion)

    outputDirectory.set(currentDocsDir)
    pluginConfiguration<VersioningPlugin, VersioningConfiguration> {
        olderVersionsDir = docVersionsDir
        version = currentVersion
    }

    doLast {
        docsDir
            .resolve("docs")
            .apply {
                deleteRecursively()
                currentDocsDir.copyRecursively(this, overwrite = true)
            }

        currentDocsDir
            .resolve("older")
            .deleteRecursively()
    }
}

val htmlJar by tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier = "html-docs"
}

tasks.withType<DokkaTask>().configureEach {
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(file("dokka/assets/github.svg"), file("dokka/assets/home.svg"))
        customStyleSheets = listOf(file("dokka/styleSheets/custom.css"))
        templatesDir = file("dokka/templates")
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lengors/test-engine")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("GitHub") {
            pom {
                name = "test-engine"
                description = "Test Kotlin package"

                licenses {
                    license {
                        name = "AGPL-3.0-only"
                        url = "https://www.gnu.org/licenses/agpl-3.0.txt"
                    }
                }

                url = "https://github.com/lengors/test-engine"

                issueManagement {
                    system = "Github"
                    url = "https://github.com/lengors/test-engine/issues"
                }

                scm {
                    connection = "https://github.com/lengors/test-engine.git"
                    url = "https://github.com/lengors/test-engine"
                }

                developers {
                    developer {
                        name = "lengors"
                        email = "24527258+lengors@users.noreply.github.com"
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PASSPHRASE"))
    sign(publishing.publications)
}
