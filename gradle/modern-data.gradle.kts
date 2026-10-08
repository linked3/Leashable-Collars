import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// The other direction from gradle/legacy-data.gradle.kts: the tree is committed in 1.21.11 form, so
// a node above it needs the datapack rewritten forwards. JSON again, out of Stonecutter's reach.
val modernDataMode = project.extra["modernDataMode"] as String

/**
 * 26.3 folded the loot-condition list into one optional `condition`, renamed the dispatch key from
 * `condition` to `type`, and replaced `block_state_property` with `match_block`.
 */
@Suppress("UNCHECKED_CAST")
fun upgradeConditions(node: Any?): Any? = when (node) {
    is Map<*, *> -> {
        val map = node as MutableMap<String, Any?>
        map.keys.toList().forEach { map[it] = upgradeConditions(map[it]) }
        (map["condition"] as? String)?.let { map.remove("condition"); map["type"] = it }
        if (map["type"] == "minecraft:block_state_property") {
            map["type"] = "minecraft:match_block"
            map.remove("block")?.let { map["blocks"] = it }
            map.remove("properties")?.let { map["state"] = it }
        }
        (map["conditions"] as? List<Any?>)?.let { terms ->
            map.remove("conditions")
            map["condition"] = if (terms.size == 1) terms[0]
            else mapOf("type" to "minecraft:all_of", "terms" to terms)
        }
        map
    }
    is List<*> -> node.map { upgradeConditions(it) }
    else -> node
}

/** 26.3's `recipe_unlocked` trigger takes a HolderSet under `recipes`, not one id under `recipe`. */
@Suppress("UNCHECKED_CAST")
fun upgradeAdvancement(json: String): String {
    val root = JsonSlurper().parseText(json) as MutableMap<String, Any?>
    val criteria = root["criteria"] as? Map<String, Any?> ?: emptyMap()
    for (criterion in criteria.values) {
        val entry = criterion as? Map<String, Any?> ?: continue
        if (entry["trigger"] != "minecraft:recipe_unlocked") continue
        val conditions = entry["conditions"] as? MutableMap<String, Any?> ?: continue
        conditions.remove("recipe")?.let { conditions["recipes"] = it }
    }
    return JsonOutput.prettyPrint(JsonOutput.toJson(root))
}

fun upgradeJson(json: String) = JsonOutput.prettyPrint(JsonOutput.toJson(upgradeConditions(JsonSlurper().parseText(json))))

val modernData = tasks.register("modernData") {
    description = "Rewrites the shared datapack into the form this node's Minecraft can read."
    val source = rootProject.file("src/main/resources/data")
    val outDir = layout.buildDirectory.dir("generated/modern-data/data")
    inputs.dir(source)
    inputs.property("mode", modernDataMode)
    outputs.dir(outDir)
    doLast {
        val out = outDir.get().asFile
        out.deleteRecursively()
        source.walkTopDown().filter { it.isFile }.forEach { file ->
            val original = "data/" + file.relativeTo(source).invariantSeparatorsPath
            val target = File(out.parentFile, original)
            target.parentFile.mkdirs()
            // An advancement criterion's `conditions` is an object, not a list of loot conditions.
            val json = file.extension == "json"
            when {
                json && (original.contains("/loot_table/") || original.contains("/enchantment/")) ->
                    target.writeText(upgradeJson(file.readText()))
                json && original.contains("/advancement/") -> target.writeText(upgradeAdvancement(file.readText()))
                else -> file.copyTo(target, overwrite = true)
            }
        }
    }
}

if (modernDataMode != "none") {
    tasks.named<ProcessResources>("processResources") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(layout.buildDirectory.dir("generated/modern-data"))
        dependsOn(modernData)
    }
}
