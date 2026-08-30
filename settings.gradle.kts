pluginManagement {
    repositories {
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

stonecutter {
    create(rootProject) {
        fun mc(mcVersion: String, vararg loaders: String) = loaders.forEach { loader ->
            version("$mcVersion-$loader", mcVersion).buildscript = "build.$loader.gradle.kts"
        }

        mc("1.18.2", "fabric")
        mc("1.19.2", "fabric")
        mc("1.19.4", "fabric")
        mc("1.20.1", "fabric")
        mc("1.20.4", "fabric")

        vcsVersion = "1.20.4-fabric"
    }
}

rootProject.name = "MTRFRA"
