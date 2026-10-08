import groovy.json.JsonOutput
import groovy.json.JsonSlurper

// Element rotation is JSON, so no Stonecutter guard can reach it.
val legacyModelMode = project.extra["legacyModelMode"] as String

// Below 1.21.4 nothing reads assets/playercollars/items/, so a condition becomes an `overrides` block.
val legacyItemModels = project.extra["legacyItemModels"] as Boolean

val steps = listOf(-45.0, -22.5, 0.0, 22.5, 45.0)

// Which end of the box each face corner takes, in FaceInfo's vertex order, read out of the 1.20.1 jar.
val faceVertices = mapOf(
    "down" to listOf(intArrayOf(0, 0, 1), intArrayOf(0, 0, 0), intArrayOf(1, 0, 0), intArrayOf(1, 0, 1)),
    "up" to listOf(intArrayOf(0, 1, 0), intArrayOf(0, 1, 1), intArrayOf(1, 1, 1), intArrayOf(1, 1, 0)),
    "north" to listOf(intArrayOf(1, 1, 0), intArrayOf(1, 0, 0), intArrayOf(0, 0, 0), intArrayOf(0, 1, 0)),
    "south" to listOf(intArrayOf(0, 1, 1), intArrayOf(0, 0, 1), intArrayOf(1, 0, 1), intArrayOf(1, 1, 1)),
    "west" to listOf(intArrayOf(0, 1, 0), intArrayOf(0, 0, 0), intArrayOf(0, 0, 1), intArrayOf(0, 1, 1)),
    "east" to listOf(intArrayOf(1, 1, 1), intArrayOf(1, 0, 1), intArrayOf(1, 0, 0), intArrayOf(1, 1, 0)),
)

// A right-handed quarter turn about each axis, as the cycle it puts the four side faces through.
val faceCycles = mapOf(
    "x" to listOf("up", "south", "down", "north"),
    "y" to listOf("east", "north", "west", "south"),
    "z" to listOf("east", "up", "west", "down"),
)

// The two coordinates a rotation about each axis moves, ordered so the turn comes out right-handed.
val axisPlanes = mapOf("x" to (1 to 2), "y" to (2 to 0), "z" to (0 to 1))

fun num(value: Any?) = (value as Number).toDouble()

fun turnFace(axis: String, turns: Int, face: String): String {
    val cycle = faceCycles.getValue(axis)
    val index = cycle.indexOf(face)
    return if (index < 0) face else cycle[(index + turns) % 4]
}

fun turnPoint(axis: String, turns: Int, origin: List<Double>, point: DoubleArray): DoubleArray {
    val plane = axisPlanes.getValue(axis)
    var u = point[plane.first] - origin[plane.first]
    var v = point[plane.second] - origin[plane.second]
    repeat(turns) {
        val swept = -v
        v = u
        u = swept
    }
    val out = point.copyOf()
    out[plane.first] = origin[plane.first] + u
    out[plane.second] = origin[plane.second] + v
    return out
}

fun corner(from: DoubleArray, to: DoubleArray, pick: IntArray) = doubleArrayOf(
    if (pick[0] == 0) from[0] else to[0],
    if (pick[1] == 0) from[1] else to[1],
    if (pick[2] == 0) from[2] else to[2])

/** How far the baked turn shifts one face's corners, which is what the UV rotation has to absorb. */
fun cornerShift(axis: String, turns: Int, origin: List<Double>, from: DoubleArray, to: DoubleArray,
                newFrom: DoubleArray, newTo: DoubleArray, face: String, newFace: String): Int {
    val turned = faceVertices.getValue(face).map { turnPoint(axis, turns, origin, corner(from, to, it)) }
    val wanted = faceVertices.getValue(newFace).map { corner(newFrom, newTo, it) }
    for (shift in 0 until 4) {
        val fits = wanted.indices.all { i ->
            (0 until 3).all { c -> Math.abs(wanted[i][c] - turned[(i + shift) % 4][c]) < 1.0e-4 }
        }
        if (fits) return shift
    }
    throw GradleException("No quarter-turn UV rotation carries $face onto $newFace about $axis")
}

/** An element's rotation as axis and angle, in either the old `angle`/`axis` form or the new one. */
fun rotationAxisAngle(rotation: Map<*, *>): Pair<String, Double> {
    if (rotation.containsKey("angle")) return (rotation["axis"] as String) to num(rotation["angle"])
    val turned = listOf("x", "y", "z").filter { rotation[it] != null && num(rotation[it]) != 0.0 }
    if (turned.size > 1) throw GradleException("Element turns about ${turned.size} axes; no single-axis form exists")
    val axis = turned.firstOrNull() ?: "y"
    return axis to (rotation[axis]?.let { num(it) } ?: 0.0)
}

@Suppress("UNCHECKED_CAST")
fun downgradeElement(element: MutableMap<String, Any?>): MutableMap<String, Any?> {
    val rotation = element["rotation"] as? Map<String, Any?> ?: return element
    val axisAngle = rotationAxisAngle(rotation)
    val axis = axisAngle.first
    val angle = axisAngle.second
    val origin = (rotation["origin"] as? List<Any?>)?.map { num(it) } ?: listOf(8.0, 8.0, 8.0)
    val quarters = Math.round(angle / 90.0).toInt()
    val turns = Math.floorMod(quarters, 4)
    val residual = angle - 90.0 * quarters
    val kept = if (legacyModelMode == "stepped") steps.minByOrNull { Math.abs(it - residual) }!! else residual

    val from = (element["from"] as List<Any?>).map { num(it) }.toDoubleArray()
    val to = (element["to"] as List<Any?>).map { num(it) }.toDoubleArray()
    val turned = (0 until 8).map {
        turnPoint(axis, turns, origin, corner(from, to, intArrayOf(it and 1, (it shr 1) and 1, (it shr 2) and 1)))
    }
    val newFrom = DoubleArray(3) { c -> turned.minOf { it[c] } }
    val newTo = DoubleArray(3) { c -> turned.maxOf { it[c] } }

    val newFaces = linkedMapOf<String, Any?>()
    for ((face, data) in element["faces"] as Map<String, Any?>) {
        val copy = LinkedHashMap(data as Map<String, Any?>)
        val newFace = turnFace(axis, turns, face)
        val shift = cornerShift(axis, turns, origin, from, to, newFrom, newTo, face, newFace)
        val uvRotation = (((copy["rotation"] as? Number)?.toInt() ?: 0) + 90 * shift) % 360
        if (uvRotation == 0) copy.remove("rotation") else copy["rotation"] = uvRotation
        (copy["cullface"] as? String)?.let { copy["cullface"] = turnFace(axis, turns, it) }
        newFaces[newFace] = copy
    }

    element["from"] = newFrom.toList()
    element["to"] = newTo.toList()
    element["faces"] = newFaces
    if (kept == 0.0) {
        element.remove("rotation")
    } else {
        val turn = linkedMapOf<String, Any?>("angle" to kept, "axis" to axis, "origin" to origin)
        (rotation["rescale"] as? Boolean)?.let { turn["rescale"] = it }
        element["rotation"] = turn
    }
    return element
}

fun unreadable(rotation: Map<*, *>): Boolean {
    if (!rotation.containsKey("angle")) return true
    val angle = num(rotation["angle"])
    return if (legacyModelMode == "stepped") steps.none { it == angle } else Math.abs(angle) > 45.0
}

/** Each `using_item` swap in `items/`, as the model it swaps *from* mapped to the one it swaps *to*. */
@Suppress("UNCHECKED_CAST")
fun usingItemSwaps(itemsDir: File): Map<String, String> {
    val swaps = linkedMapOf<String, String>()
    itemsDir.listFiles { file: File -> file.extension == "json" }?.forEach { file ->
        val root = JsonSlurper().parseText(file.readText()) as Map<String, Any?>
        val model = root["model"] as? Map<String, Any?> ?: return@forEach
        if (model["type"] != "minecraft:condition" || model["property"] != "minecraft:using_item") return@forEach
        val on = (model["on_true"] as? Map<String, Any?>)?.get("model") as? String ?: return@forEach
        val off = (model["on_false"] as? Map<String, Any?>)?.get("model") as? String ?: return@forEach
        swaps[off.substringAfterLast('/')] = on
    }
    return swaps
}

@Suppress("UNCHECKED_CAST")
fun rewriteModel(json: String, swapTo: String?): String? {
    val root = JsonSlurper().parseText(json) as MutableMap<String, Any?>
    var changed = false
    val elements = root["elements"] as? List<Any?>
    if (legacyModelMode != "none" && elements != null
        && elements.mapNotNull { (it as Map<String, Any?>)["rotation"] as? Map<*, *> }.any { unreadable(it) }) {
        root["elements"] = elements.map { downgradeElement(LinkedHashMap(it as Map<String, Any?>)) }
        changed = true
    }
    if (swapTo != null) {
        root["overrides"] = listOf(mapOf(
            "predicate" to mapOf("playercollars:using_item" to 1),
            "model" to swapTo))
        changed = true
    }
    return if (changed) JsonOutput.prettyPrint(JsonOutput.toJson(root)) else null
}

val legacyAssets = tasks.register("legacyAssets") {
    description = "Rewrites item models this node's Minecraft cannot read as committed."
    val source = rootProject.file("src/main/resources/assets/playercollars/models/item")
    val items = rootProject.file("src/main/resources/assets/playercollars/items")
    val outDir = layout.buildDirectory.dir("generated/legacy-assets/assets/playercollars/models/item")
    inputs.dir(source)
    inputs.dir(items)
    inputs.property("mode", legacyModelMode)
    inputs.property("itemModels", legacyItemModels)
    outputs.dir(outDir)
    doLast {
        val swaps = if (legacyItemModels) usingItemSwaps(items) else emptyMap()
        val out = outDir.get().asFile
        out.deleteRecursively()
        out.mkdirs()
        source.listFiles { file: File -> file.extension == "json" }?.forEach { file ->
            rewriteModel(file.readText(), swaps[file.nameWithoutExtension])?.let { out.resolve(file.name).writeText(it) }
        }
    }
}

if (legacyModelMode != "none" || legacyItemModels) {
    tasks.named<ProcessResources>("processResources") {
        // Registered after the main source set, so these win the name clash.
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(layout.buildDirectory.dir("generated/legacy-assets"))
        dependsOn(legacyAssets)
    }
}
