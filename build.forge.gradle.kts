plugins {
    id("net.neoforged.moddev.legacyforge")
}

val mc = stonecutter.current.version

group = property("mod.group").toString()
version = "${property("mod.version")}+$mc"
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
    mavenCentral()
}

dependencies {
    // Curios is the accessory API on Forge/NeoForge. The 1.20.1 line predates both
    // maven.theillusivec4.top and the api/full jar split, so it comes off CurseMaven as one artifact --
    // the same coordinate the shipping legacy module resolves.
    findProperty("deps.curios")?.let { implementation("curse.maven:curios-309927:$it") }
}

legacyForge {
    version = "$mc-${property("deps.forge")}"

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

// javac stops at 100 errors by default, which hides most of the picture when a node first joins the
// shared tree.
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000"))
}

tasks.named("compileJava") { dependsOn("stonecutterGenerate") }
tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }

// Tagged for both Forge and NeoForge 1.20.1 -- that NeoForge line is a soft fork of Forge 47.x and
// kept the net.minecraftforge packages and META-INF/mods.toml.
val modProps = mapOf(
    "id" to property("mod.id"),
    "name" to property("mod.name"),
    "version" to version,
    "license" to property("mod.license"),
    "authors" to property("mod.authors"),
    "description" to property("mod.description"),
    // Declared Minecraft range for this jar.
    "minecraft" to property("mc.range.forge"),
    "loader" to property("deps.forge.range"),
    "java" to property("java.version"),
)

tasks.named<ProcessResources>("processResources") {
    inputs.properties(modProps)
    // One shared resource tree; each loader drops the others' metadata. See build.fabric.gradle.kts.
    exclude("fabric.mod.json", "*.accesswidener", "META-INF/neoforge.mods.toml")
    filesMatching("META-INF/mods.toml") { expand(modProps) }
}
