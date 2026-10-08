@file:OptIn(dev.kikugie.stonecutter.StonecutterExperimentalAPI::class)

plugins {
    id("dev.kikugie.stonecutter")
    // 26.1 dropped obfuscation, so remap vs no-remap Loom is per node; build.fabric.gradle.kts picks.
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
    // MDG legacy covers Forge 1.17-1.20.1 on modern Gradle, so one wrapper serves every target.
    id("net.neoforged.moddev") version "2.0.148" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.148" apply false
}

stonecutter active "1.21.11-fabric"

// Nodes where Accessories ships for Forge/NeoForge too, so the jar binds both and picks at runtime.
// TODO(plan 0.5.1): 1.21.1, 1.21.4, 1.21.5, 1.21.8 NeoForge still to follow.
val dualLibraryNodes = setOf(
    "1.20.1-forge",
)

stonecutter parameters {
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
    constants["dual"] = current.project in dualLibraryNodes
    swaps["mod_version"] = "\"${property("mod.version")}\";"
    swaps["mod_id"] = "\"${property("mod.id")}\";"
    swaps["mod_name"] = "\"${property("mod.name")}\";"
    swaps["minecraft"] = "\"${current.version}\";"
}

tasks.register("chiseledBuild") {
    group = "stonecutter"
    description = "Builds every version node."
    dependsOn(stonecutter.tasks.named("build"))
}

tasks.register("chiseledAssemble") {
    group = "stonecutter"
    description = "Assembles the jar for every version node."
    dependsOn(stonecutter.tasks.named("assemble"))
}

tasks.register("chiseledClean") {
    group = "stonecutter"
    description = "Cleans every version node."
    dependsOn(stonecutter.tasks.named("clean"))
}

// Censuses the whole matrix in one pass; red during the migration, `chiseledBuild` is the green gate.
tasks.register("chiseledCompile") {
    group = "stonecutter"
    description = "Compiles (javac only) every declared version node. Use with --continue."
    dependsOn(stonecutter.tasks.named("compileJava"))
}
