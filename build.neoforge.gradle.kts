plugins {
    id("net.neoforged.moddev")
}

val mc = stonecutter.current.version

group = property("mod.group").toString()
version = property("mod.version").toString()
base.archivesName = "${property("mod.id")}-neoforge-$mc"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(property("java.version").toString().toInt())
    withSourcesJar()
}

repositories {
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://maven.theillusivec4.top/") { name = "Illusive Soulworks (Curios)" }
    mavenCentral()
}

neoForge {
    version = property("deps.neoforge").toString()

    // Mojang names on every node here, so unlike the Fabric widener there's no header to rewrite.
    accessTransformers.from(rootProject.file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        register("client") {
            client()
            gameDirectory = file("run")
            ideName = "NeoForge Client ($mc)"
        }
        register("server") {
            server()
            gameDirectory = file("run")
            ideName = "NeoForge Server ($mc)"
        }
    }

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }
}

dependencies {
    // Curios splits its jar: compile against the `api` classifier, need the full one only at runtime.
    findProperty("deps.curios")?.let {
        compileOnly("top.theillusivec4.curios:curios-neoforge:$it:api")
        runtimeOnly("top.theillusivec4.curios:curios-neoforge:$it")
    }
}

tasks.named("compileJava") { dependsOn("stonecutterGenerate") }
tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }

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
    "minecraft" to property("mc.range.neoforge"),
    "loader" to property("deps.neoforge.range"),
    "java" to property("java.version"),
    // Curios' major tracks Minecraft, so the declared range is per node.
    "curios" to property("deps.curios.range"),
    // NeoForge 26.2 deprecated logoFile in favour of a square iconFile and a wide bannerFile.
    "logokey" to if (stonecutter.eval(mc, ">=26.2")) "iconFile" else "logoFile",
)

tasks.named<ProcessResources>("processResources") {
    inputs.properties(modProps)
    // One shared resource tree; each loader drops the others' metadata. See build.fabric.gradle.kts.
    exclude("fabric.mod.json", "*.accesswidener", "META-INF/mods.toml")
    // Slot definitions are data files; this loader's library is Curios.
    exclude("data/accessories/**", "data/trinkets/**")
    // compatibilityLevel tracks this node's toolchain; Mixin refuses a level above the running JVM.
    filesMatching(listOf("META-INF/neoforge.mods.toml", "*.mixins.json")) { expand(modProps) }
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
