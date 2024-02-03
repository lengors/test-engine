pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings
    val dokkaVersion: String by settings
    val ktlintVersion: String by settings
    val koverVersion: String by settings

    plugins {
        kotlin("multiplatform") version kotlinVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
        id("org.jetbrains.kotlinx.kover") version koverVersion
    }
}

rootProject.name = "test-engine"
