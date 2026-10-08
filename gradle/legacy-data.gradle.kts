import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// Datapack layout and recipe syntax are JSON, so no Stonecutter guard can reach them.
val legacyDataMode = project.extra["legacyDataMode"] as String

fun pluralize(path: String) = path
    .replace(Regex("^data/([^/]+)/recipe/"), "data/$1/recipes/")
    .replace(Regex("^data/([^/]+)/advancement/"), "data/$1/advancements/")
    .replace(Regex("^data/([^/]+)/loot_table/"), "data/$1/loot_tables/")
    .replace(Regex("^data/([^/]+)/tags/item/"), "data/$1/tags/items/")
    .replace(Regex("^data/([^/]+)/tags/block/"), "data/$1/tags/blocks/")

/** A bare id was an object before 1.21.2: `"#c:dyes"` meant a tag, anything else an item. */
fun ingredient(value: Any?): Any? = when (value) {
    is String ->
        if (value.startsWith("#")) mapOf("tag" to value.substring(1)) else mapOf("item" to value)
    is List<*> -> value.map { ingredient(it) }
    else -> value
}

// 1.21 moved ingredients to a bare id but kept `id` in the result; below 1.21 it's `item`.
@Suppress("UNCHECKED_CAST")
fun downgradeRecipe(json: String, resultUsesItem: Boolean): String {
    val root = JsonSlurper().parseText(json) as MutableMap<String, Any?>
    if (resultUsesItem) (root["result"] as? MutableMap<String, Any?>)?.let { result ->
        result.remove("id")?.let { result["item"] = it }
    }
    (root["key"] as? MutableMap<String, Any?>)?.let { key ->
        key.keys.toList().forEach { key[it] = ingredient(key[it]) }
    }
    (root["ingredients"] as? List<Any?>)?.let { root["ingredients"] = it.map { entry -> ingredient(entry) } }
    for (field in listOf("base", "ingredient", "addition", "template")) {
        if (root.containsKey(field)) root[field] = ingredient(root[field])
    }
    return JsonOutput.prettyPrint(JsonOutput.toJson(root))
}

/** 1.20.1's item predicate takes a list under `items`, or a tag under its own key. */
@Suppress("UNCHECKED_CAST")
fun downgradeAdvancement(json: String): String {
    val root = JsonSlurper().parseText(json) as MutableMap<String, Any?>
    val criteria = root["criteria"] as? Map<String, Any?> ?: return JsonOutput.prettyPrint(JsonOutput.toJson(root))
    for (criterion in criteria.values) {
        val conditions = (criterion as? Map<String, Any?>)?.get("conditions") as? Map<String, Any?> ?: continue
        val predicates = conditions["items"] as? List<Any?> ?: continue
        for (predicate in predicates) {
            val entry = predicate as? MutableMap<String, Any?> ?: continue
            val id = entry["items"] as? String ?: continue
            entry.remove("items")
            if (id.startsWith("#")) entry["tag"] = id.substring(1) else entry["items"] = listOf(id)
        }
    }
    return JsonOutput.prettyPrint(JsonOutput.toJson(root))
}

/** Curios drew slot icons off the block atlas until 10.0; from there they are GUI sprites. */
fun downgradeCuriosSlot(json: String) =
    json.replace("minecraft:container/slot/boots", "minecraft:item/empty_armor_slot_boots")

val legacyData = tasks.register("legacyData") {
    description = "Rewrites the shared datapack into the form this node's Minecraft can read."
    val source = rootProject.file("src/main/resources/data")
    val outDir = layout.buildDirectory.dir("generated/legacy-data/data")
    val plural = legacyDataMode == "all"
    inputs.dir(source)
    inputs.property("mode", legacyDataMode)
    outputs.dir(outDir)
    doLast {
        val out = outDir.get().asFile
        out.deleteRecursively()
        source.walkTopDown().filter { it.isFile }.forEach { file ->
            val original = "data/" + file.relativeTo(source).invariantSeparatorsPath
            val target = File(out.parentFile, if (plural) pluralize(original) else original)
            target.parentFile.mkdirs()
            val json = file.extension == "json"
            when {
                json && original.contains("/recipe/") -> target.writeText(downgradeRecipe(file.readText(), plural))
                json && plural && original.contains("/advancement/") -> target.writeText(downgradeAdvancement(file.readText()))
                json && original.contains("/curios/slots/") -> target.writeText(downgradeCuriosSlot(file.readText()))
                else -> file.copyTo(target, overwrite = true)
            }
        }
    }
}

if (legacyDataMode != "none") {
    tasks.named<ProcessResources>("processResources") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(layout.buildDirectory.dir("generated/legacy-data"))
        dependsOn(legacyData)
    }
}
