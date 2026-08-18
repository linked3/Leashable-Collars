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

// Only finished nodes are declared by default, so `chiseledBuild` stays a real green gate.
// `-Pmigration=all` adds every node that has its pins, which gets the whole-matrix error census in one
// invocation instead of one un-park/build/re-park cycle per node:
//     ./gradlew -Pmigration=all chiseledCompile --continue
val migrateAll = startParameter.projectProperties["migration"] == "all"

stonecutter {
    create(rootProject) {
        // The loader half of the node name becomes a preprocessor constant, so `//? if fabric` works
        // in the shared source tree.
        fun match(version: String, vararg loaders: String) =
            loaders.forEach { version("$version-$it", version).buildscript = "build.$it.gradle.kts" }

        // Finished nodes -- these are what `chiseledBuild` must keep green.
        match("1.21.11", "fabric")
        match("1.21.11", "neoforge")

        // In-flight nodes: declared only under -Pmigration=all. Each has its pins in
        // versions/<node>/gradle.properties and reaches javac; none is green yet.
        //   1.21.1-neoforge  0.6.6, 143 errors
        //   1.21.1-fabric    0.6.6
        //   1.20.1-forge     0.6.7
        if (migrateAll) {
            match("1.21.1", "neoforge")
            match("1.21.1", "fabric")
            match("1.20.1", "forge")
        }

        // PARKED (2026-08-11): no accessory library is wired for 26.1 yet. Its legacy module at
        // versions/26.1.2/fabric/ keeps its own wrapper and stays buildable.
        // match("26.1.2", "fabric")

        // The version the shared source tree is committed as. Run the "Reset active project"
        // task before committing so diffs stay readable.
        vcsVersion = "1.21.11-fabric"
    }
}

rootProject.name = "leashable-collars"
