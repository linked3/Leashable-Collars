plugins {
    id("net.neoforged.moddev")
}

val mc = stonecutter.current.version

group = property("mod.group").toString()
version = "${property("mod.version")}+$mc"
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

    // NeoForge's counterpart to the Fabric access widener. Same single widen, also in Mojang names, but
    // no per-node header to rewrite.
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
    // As on Fabric, nodes opt into an accessory library by declaring the property; here it is Curios.
    // Curios splits its jar -- mods compile against the `api` classifier and need the full one only at
    // runtime.
    findProperty("deps.curios")?.let {
        compileOnly("top.theillusivec4.curios:curios-neoforge:$it:api")
        runtimeOnly("top.theillusivec4.curios:curios-neoforge:$it")
    }
}

tasks.named("compileJava") { dependsOn("stonecutterGenerate") }
tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }

// javac's 100-error default hides most of the picture when a node first joins the shared tree.
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
    // Declared Minecraft range for this jar.
    "minecraft" to property("mc.range.neoforge"),
    "loader" to property("deps.neoforge.range"),
    "java" to property("java.version"),
)

tasks.named<ProcessResources>("processResources") {
    inputs.properties(modProps)
    // One shared resource tree; each loader drops the others' metadata. See build.fabric.gradle.kts.
    exclude("fabric.mod.json", "*.accesswidener", "META-INF/mods.toml")
    // Slot definitions are data files, and this loader's library is Curios.
    exclude("data/accessories/**")
    // compatibilityLevel comes from this node's toolchain -- Mixin refuses a level above the JVM.
    filesMatching(listOf("META-INF/neoforge.mods.toml", "*.mixins.json")) { expand(modProps) }
}
