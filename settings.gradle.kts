// Can not be configured by Conventions-Plugin.
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    val versionMyJavaConventionPlugin = providers.gradleProperty("version_myJavaConventionPlugin")
    val versionSpringBoot = providers.gradleProperty("version_springBoot")
    val versionJavaFxPlugin = providers.gradleProperty("version_javafxPlugin")

    plugins {
        id("de.freese.gradle.conventions").version(versionMyJavaConventionPlugin).apply(false)
        id("org.springframework.boot").version(versionSpringBoot).apply(false)
        id("org.openjfx.javafxplugin").version(versionJavaFxPlugin).apply(false)
    }
}

// Without rootProject.name the Name of the Projekt-Directory is used.
rootProject.name = "pim"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

include("pim-core")
include("pim-server")
include("pim-clientfx")
