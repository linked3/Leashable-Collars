import net.fabricmc.loom.api.LoomGradleExtensionAPI

// 26.1+ ships unobfuscated, so the Loom plugin id and the dependency notation both differ per node.
val obfuscated = property("mc.obfuscated").toString().toBoolean()
apply(plugin = if (obfuscated) "net.fabricmc.fabric-loom-remap" else "net.fabricmc.fabric-loom")

val loom = extensions.getByType(LoomGradleExtensionAPI::class)
val mc = stonecutter.current.version

// Not preprocessed, so read from the root path. Loom wants the v2 header to name the namespace it
// reads in, and that flips at the 26.1 obfuscation boundary.
val awSource = rootProject.file("src/main/resources/playercollars.accesswidener")
val awNamespace = if (obfuscated) "named" else "official"

// Loom validates every line against this node's Minecraft, so the other era's PlayerModel must go.
val staleModelClass =
    if (stonecutter.eval(mc, ">=1.21.11")) "net/minecraft/client/model/PlayerModel "
    else "net/minecraft/client/model/player/PlayerModel "

fun retargetAccessWidener(text: String) =
    text.lineSequence()
        .filterNot { it.contains(staleModelClass) }
        .filterNot { stonecutter.eval(mc, ">=1.19") && it.contains("net/minecraft/client/model/geom/ModelPart cubes ") }
        .filterNot { stonecutter.eval(mc, ">=1.19.3") && it.contains("net/minecraft/client/renderer/RenderType create ") }
        .joinToString("\n")
        .replaceFirst(Regex("^accessWidener\\s+v2\\s+\\S+"), "accessWidener v2 $awNamespace")

val awGenerated = layout.buildDirectory.file("generated/playercollars.accesswidener").get().asFile
awGenerated.parentFile.mkdirs()
awGenerated.writeText(retargetAccessWidener(awSource.readText()))
loom.accessWidenerPath.set(awGenerated)

group = property("mod.group").toString()
version = property("mod.version").toString()
base.archivesName = "${property("mod.id")}-fabric-$mc"

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
    maven("https://maven.wispforest.io/releases") { name = "Wisp Forest" }
    maven("https://maven.nucleoid.xyz/releases") { name = "Nucleoid" }
    if (project.hasProperty("deps.trinkets.legacy")) {
        maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" }
        maven("https://maven.ladysnake.org/releases") { name = "Ladysnake" }
    }
    // Accessories' 1.20.1 backport pulls cloth-config, published here and nowhere else.
    maven("https://maven.shedaniel.me") { name = "Shedaniel" }
    // owo-lib 0.13 pulls kdl4j, which is published nowhere else.
    maven("https://jitpack.io") { name = "JitPack" }
    // Pin deps.accessories.local to test a local build of the 26.x port before uploading it.
    if (hasProperty("deps.accessories.local")) {
        mavenLocal { content { includeModule("io.wispforest", "accessories-fabric") } }
    }
    mavenCentral()
}

dependencies {
    add("minecraft", "com.mojang:minecraft:$mc")
    if (obfuscated) add("mappings", loom.officialMojangMappings())
    // Only the remap plugin creates modImplementation, which also drags in sponge-mixin.
    val modImpl = if (obfuscated) "modImplementation" else "implementation"
    add(modImpl, "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    add(modImpl, "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    // No accessory library spans the whole range, so each node opts in by declaring the property.
    findProperty("deps.trinkets")?.let { add(modImpl, "eu.pb4:trinkets:$it") }
    findProperty("deps.trinkets.legacy")?.let {
        add(modImpl, "dev.emi:trinkets:$it") {
            // Upstream POM includes its dev-only menu.
            exclude(group = "com.terraformersmc", module = "modmenu")
        }
        // Trinkets nests these at runtime; javac needs the component superinterfaces.
        for (module in listOf("base", "entity")) {
            add(modImpl, "dev.onyxstudios.cardinal-components-api:cardinal-components-$module:${property("deps.cardinal_components")}")
        }
    }
    findProperty("deps.accessories")?.let {
        add(modImpl, "io.wispforest:accessories-fabric:$it") {
            // The 1.21.9 Accessories jar pulls a 1.21.10 Fabric API through its POM.
            if (mc == "1.21.9") exclude(group = "net.fabricmc.fabric-api")
        }
    }
    // Same coordinate, resolved from the port above rather than from Wisp Forest.
    findProperty("deps.accessories.local")?.let { add(modImpl, "io.wispforest:accessories-fabric:$it") }
    // The 1.21.11 and 26.x ports ship only on CurseForge, pinned "<slug>-<projectId>:<fileId>".
    findProperty("deps.accessories.curse")?.let { add(modImpl, "curse.maven:$it") }
    // The port doesn't nest owo-lib, so the dev runtime needs it.
    findProperty("deps.owo")?.let { add(modImpl, "io.wispforest:owo-lib:$it") }
}

tasks.named("compileJava") { dependsOn("stonecutterGenerate") }

// javac stops at 100 errors by default, too few when a node first joins the tree.
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000"))
}

val modProps = mapOf(
    "id" to property("mod.id"),
    "name" to property("mod.name"),
    "version" to version,
    "license" to property("mod.license"),
    "authors" to property("mod.authors"),
    "description" to property("mod.description"),
    "minecraft" to property("mc.range.fabric"),
    "loader" to property("deps.fabric_loader"),
    "java" to property("java.version"),
    "equipment" to if (hasProperty("deps.trinkets.legacy")) "trinkets" else "accessories",
    "equipment_version" to (findProperty("deps.trinkets.legacy")?.let { ">=$it <3.4" } ?: "*"),
)

tasks.named<ProcessResources>("processResources") {
    inputs.properties(modProps)
    // One shared resource tree, so each loader drops the others' metadata on the way into the jar.
    exclude("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "META-INF/accesstransformer.cfg")
    // Slot definitions are data files; the other library's would declare unfillable slots.
    exclude("data/curios/**", "data/playercollars/curios/**")
    if (project.hasProperty("deps.trinkets.legacy")) {
        exclude("data/accessories/**")
    } else {
        exclude("data/trinkets/**")
    }
    // compatibilityLevel tracks this node's toolchain; Mixin refuses a level above the running JVM.
    filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(modProps) }
    // remapJar reads the widener back out of the jar resources, so retarget here too.
    filesMatching("playercollars.accesswidener") { filter { line -> retargetAccessWidener(line) } }
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
