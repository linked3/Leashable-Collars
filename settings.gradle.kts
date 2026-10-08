pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
}

// Finished nodes only by default, so `chiseledBuild` stays a real gate; `-Pmigration=all` adds the
// parked ones for a whole-matrix census.
val migrateAll = startParameter.projectProperties["migration"] == "all"

stonecutter {
    create(rootProject) {
        // Loader half of the node name becomes a preprocessor constant, so `//? if fabric` works.
        fun match(version: String, vararg loaders: String) =
            loaders.forEach { version("$version-$it", version).buildscript = "build.$it.gradle.kts" }

        // Finished nodes -- `chiseledBuild` keeps these green.
        // 26.3's pins are both betas, and NeoForge is held at .36 on purpose -- see
        // versions/26.3-neoforge/gradle.properties.
        match("26.3", "neoforge")
        match("26.2", "fabric", "neoforge")
        match("26.1.2", "fabric", "neoforge")
        match("1.21.11", "fabric")
        match("1.21.11", "neoforge")
        match("1.21.9", "fabric", "neoforge")
        match("1.21.10", "fabric") // Ceiling canary; lower-band jar ships from 1.21.9.
        match("1.21.8", "fabric", "neoforge")
        match("1.21.5", "fabric", "neoforge")
        match("1.21.4", "fabric", "neoforge")
        match("1.21.1", "neoforge")
        match("1.21.1", "fabric")
        match("1.20.1", "forge")
        match("1.20.1", "fabric")

        // Promoted once build and boot pass. 26.3-fabric stays out even of this list -- no accessory
        // API exists there, and 202 permanent errors would kill the census signal.
        val inFlight = listOf(
            "1.18.2" to "fabric",
            "1.18.2" to "forge",
        )
        if (migrateAll) inFlight.forEach { (version, loader) -> match(version, loader) }

        // Version the tree is committed as; run "Reset active project" before committing.
        vcsVersion = "1.21.11-fabric"
    }
}

rootProject.name = "leashable-collars"
