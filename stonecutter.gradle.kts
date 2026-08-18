@file:OptIn(dev.kikugie.stonecutter.StonecutterExperimentalAPI::class)

plugins {
    id("dev.kikugie.stonecutter")
    // Which Loom plugin id is right depends on whether the target Minecraft is obfuscated. 26.1 is the
    // first release that is not -- no client_mappings, and Yarn/Intermediary stop after 1.21.11.
    //   <= 1.21.11 -> fabric-loom-remap, with loom.officialMojangMappings()
    //   >= 26.1    -> fabric-loom (no-remap), no mappings at all
    // build.fabric.gradle.kts picks per node.
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
    // MDG's legacy plugin covers Forge 1.17-1.20.1 and NeoForge 1.20.1 and runs on modern Gradle,
    // which is the only reason one wrapper can serve every target. Hence no ForgeGradle anywhere.
    id("net.neoforged.moddev") version "2.0.143" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.143" apply false
}

stonecutter active "1.21.11-fabric"

stonecutter parameters {
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
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

// Stops at javac, skipping remapJar/reobfJar/tests. With `-Pmigration=all --continue` it censuses the
// whole matrix at once, which catches guards written for node N that broke node N-1. Expected to be
// red during the migration; `chiseledBuild` stays the green gate.
tasks.register("chiseledCompile") {
    group = "stonecutter"
    description = "Compiles (javac only) every declared version node. Use with --continue."
    dependsOn(stonecutter.tasks.named("compileJava"))
}
