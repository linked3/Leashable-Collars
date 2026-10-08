plugins {
    id("net.neoforged.moddev.legacyforge")
}

val mc = stonecutter.current.version

group = property("mod.group").toString()
version = property("mod.version").toString()
base.archivesName = "${property("mod.id")}-forge-$mc"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(property("java.version").toString().toInt())
    withSourcesJar()
}

repositories {
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent {
        forRepository { maven("https://cursemaven.com") { name = "CurseMaven" } }
        filter { includeGroup("curse.maven") }
    }
    maven("https://maven.blamejared.com") { name = "BlameJared (Curios)" }
    maven("https://maven.wispforest.io") { name = "Wisp Forest (Accessories)" }
    maven("https://maven.shedaniel.me") { name = "Shedaniel (Cloth Config)" }
    mavenCentral()
}

dependencies {
    // Curios 1.20.1 predates its own maven, so it comes off CurseMaven as one artifact. Its mixins
    // carry an SRG refmap, hence the remapping configuration rather than the plain classpath.
    findProperty("deps.curios")?.let { add("modImplementation", "curse.maven:curios-309927:$it") }
    findProperty("deps.curios.modrinth")?.let { add("modImplementation", "maven.modrinth:curios:$it") }
    // One artifact covers Forge and NeoForge 1.20.1; SRG refmap again, so it remaps too.
    findProperty("deps.accessories")?.let {
        add("modImplementation", "io.wispforest:accessories-neoforge:$it")
    }
    findProperty("deps.clothconfig")?.let {
        add("modRuntimeOnly", "me.shedaniel.cloth:cloth-config-forge:$it")
    }
    // MDG wires arguments for Mixin's processor but never puts it on the processor path.
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

legacyForge {
    version = "$mc-${property("deps.forge")}"

    // Same widen as NeoForge and Fabric, but SRG-named: this loader is SRG at compile time and run.
    accessTransformers.from(rootProject.file("gradle/accesstransformer-srg.cfg"))

    runs {
        register("client") {
            client()
            gameDirectory = file("run")
            ideName = "Forge Client ($mc)"
        }
        register("server") {
            server()
            gameDirectory = file("run")
            ideName = "Forge Server ($mc)"
        }
    }

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }
}

// javac stops at 100 errors by default, too few when a node first joins the tree.
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000"))
}

// 1.20.1 runs on SRG names, so every mixin selector needs a refmap.
val refmapName = "${property("mod.id")}.refmap.json"

mixin {
    add(sourceSets["main"], refmapName)
    config("${property("mod.id")}.mixins.json")
    config("${property("mod.id")}.leashplayers.mixins.json")
}

// FML 1.20.1 ignores mods.toml's [[mixins]] block and fails silently on a config it never sees.
val mixinConfigs = listOf("mixins.json", "leashplayers.mixins.json")
    .joinToString(",") { "${property("mod.id")}.$it" }
tasks.named<Jar>("jar") {
    manifest.attributes("MixinConfigs" to mixinConfigs)
}

tasks.named("compileJava") { dependsOn("stonecutterGenerate") }
tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }

// Tagged for Forge and NeoForge 1.20.1 alike -- that NeoForge line is a soft fork of Forge 47.x.
val modProps = mapOf(
    "id" to property("mod.id"),
    "name" to property("mod.name"),
    "version" to version,
    "license" to property("mod.license"),
    "authors" to property("mod.authors"),
    "description" to property("mod.description"),
    "minecraft" to property("mc.range.forge"),
    "loader" to property("deps.forge.range"),
    "fml" to (findProperty("deps.fml.range") ?: property("deps.forge.range")),
    "java" to property("java.version"),
    // Curios' major tracks Minecraft, so the declared range is per node.
    "curios" to property("deps.curios.range"),
)

val dualLibrary = findProperty("deps.accessories") != null

// On a dual node the jar binds whichever library is installed, so neither may be mandatory.
val libraryProps = mapOf(
    "curiosMandatory" to (!dualLibrary).toString(),
    "extraDependencies" to if (!dualLibrary) "" else """

[[dependencies."${property("mod.id")}"]]
modId = "accessories"
mandatory = false
versionRange = "${property("deps.accessories.range")}"
ordering = "AFTER"
side = "BOTH"
""",
)

tasks.named<ProcessResources>("processResources") {
    val expandProps = modProps + libraryProps
    inputs.properties(expandProps)
    // One shared resource tree; each loader drops the others' metadata. See build.fabric.gradle.kts.
    exclude("fabric.mod.json", "*.accesswidener", "META-INF/neoforge.mods.toml")
    // Slot definitions are data files; shipping an unpinned library's would declare unfillable slots.
    exclude("data/trinkets/**")
    if (!dualLibrary) exclude("data/accessories/**")
    // compatibilityLevel tracks this node's toolchain; Mixin refuses a level above the running JVM.
    filesMatching(listOf("META-INF/mods.toml", "*.mixins.json")) { expand(expandProps) }
    // MDG packages the refmap but never names it in the configs, and unnamed every selector fails.
    val refmapLine = "  \"refmap\": \"$refmapName\","
    inputs.property("refmapLine", refmapLine)
    filesMatching("*.mixins.json") {
        filter { line -> if (line.trimStart().startsWith("\"package\"")) line + "\n" + refmapLine else line }
    }
    // FML reads the AT back out of the jar at runtime, where the names are SRG too.
    exclude("META-INF/accesstransformer.cfg")
    from(rootProject.file("gradle/accesstransformer-srg.cfg")) {
        into("META-INF")
        rename { "accesstransformer.cfg" }
    }
}

// See gradle/legacy-data.gradle.kts.
extra["legacyDataMode"] = when {
    stonecutter.eval(mc, ">=1.21.2") -> "none"
    stonecutter.eval(mc, ">=1.21") -> "ingredients"
    else -> "all"
}
apply(from = rootProject.file("gradle/legacy-data.gradle.kts"))

// See gradle/modern-data.gradle.kts.
extra["modernDataMode"] = if (stonecutter.eval(mc, ">=26.3")) "conditions" else "none"
apply(from = rootProject.file("gradle/modern-data.gradle.kts"))

// See gradle/legacy-assets.gradle.kts.
extra["legacyModelMode"] = when {
    stonecutter.eval(mc, ">=1.21.11") -> "none"
    stonecutter.eval(mc, ">=1.21.6") -> "singleAxis"
    else -> "stepped"
}
extra["legacyItemModels"] = stonecutter.eval(mc, "<1.21.4")
apply(from = rootProject.file("gradle/legacy-assets.gradle.kts"))

// javac never checks a Mixin selector; tools/mcsig.sh javaps the target out of this classpath.
tasks.register("printCompileClasspath") {
    val classpath = sourceSets["main"].compileClasspath
    doLast { println(classpath.asPath) }
}
