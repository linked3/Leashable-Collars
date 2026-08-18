import net.fabricmc.loom.api.LoomGradleExtensionAPI

// 26.1+ ships unobfuscated, so Loom goes in no-remap mode and rejects mappings; up to 1.21.11 needs
// the -remap plugin and Mojang mappings. The plugin id differs per node, hence the dynamic apply and
// the string-notation dependencies below.
val obfuscated = property("mc.obfuscated").toString().toBoolean()
apply(plugin = if (obfuscated) "net.fabricmc.fabric-loom-remap" else "net.fabricmc.fabric-loom")

val loom = extensions.getByType(LoomGradleExtensionAPI::class)
val mc = stonecutter.current.version

// Widens PlayerModel.slim for PawRenderer. Not preprocessed, so it is referenced at its root path
// rather than through Stonecutter's cache.
//
// The body is Mojang names everywhere, but Loom wants the header to name the namespace it is read in,
// and that straddles the 26.1 obfuscation boundary: "named" up to 1.21.11, "official" after. Cheaper
// to rewrite one header per node than to keep two near-identical files. Loom resolves Minecraft at
// configuration time, so this cannot move into a task.
val awSource = rootProject.file("src/main/resources/playercollars.accesswidener")
val awNamespace = if (obfuscated) "named" else "official"
fun retargetAccessWidener(text: String) =
    text.replaceFirst(Regex("^accessWidener\\s+v2\\s+\\S+"), "accessWidener v2 $awNamespace")

val awGenerated = layout.buildDirectory.file("generated/playercollars.accesswidener").get().asFile
awGenerated.parentFile.mkdirs()
awGenerated.writeText(retargetAccessWidener(awSource.readText()))
loom.accessWidenerPath.set(awGenerated)

group = property("mod.group").toString()
version = "${property("mod.version")}+$mc"
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
    maven("https://maven.wispforest.io/releases") { name = "Wisp Forest" }
    maven("https://maven.nucleoid.xyz/releases") { name = "Nucleoid" }
    mavenCentral()
}

dependencies {
    add("minecraft", "com.mojang:minecraft:$mc")
    if (obfuscated) add("mappings", loom.officialMojangMappings())
    // Remapped nodes want mod dependencies on "modImplementation", which is also what drags
    // sponge-mixin onto the compile classpath. The no-remap plugin never creates it.
    val modImpl = if (obfuscated) "modImplementation" else "implementation"
    add(modImpl, "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    add(modImpl, "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    // No accessory library spans our range -- Trinkets Updated starts at 26.1, Accessories stops at
    // 1.21.10 -- so nodes opt in by declaring the property instead of inheriting a version that has no
    // artifact for them.
    findProperty("deps.trinkets")?.let { add(modImpl, "eu.pb4:trinkets:$it") }
    findProperty("deps.accessories")?.let { add(modImpl, "io.wispforest:accessories-fabric:$it") }
}

tasks.named("compileJava") { dependsOn("stonecutterGenerate") }

// javac stops at 100 errors by default, which hides most of the picture when a node first joins the
// shared tree.
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
    "minecraft" to property("mc.range.fabric"),
    "loader" to property("deps.fabric_loader"),
    "java" to property("java.version"),
)

tasks.named<ProcessResources>("processResources") {
    inputs.properties(modProps)
    // One shared resource tree so Stonecutter still processes every loader's metadata; each loader
    // drops the others' on the way into the jar.
    exclude("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "META-INF/accesstransformer.cfg")
    // Slot definitions are data files, so shipping the other library's would declare slots nothing on
    // this loader can fill.
    exclude("data/curios/**", "data/playercollars/curios/**")
    // compatibilityLevel has to match this node's toolchain, not the newest in the matrix -- Mixin
    // refuses a level above the running JVM.
    filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(modProps) }
    // Same namespace retarget as above -- remapJar reads the widener back out of the jar resources.
    filesMatching("playercollars.accesswidener") { filter { line -> retargetAccessWidener(line) } }
}
