# Leashable Collars (Unofficial Port)

This is the GitHub source code repository for my unofficial port of [jlortiz0](https://github.com/jlortiz0)'s [Leashable Collars](https://modrinth.com/mod/leashable-collars) ([PlayerCollars](https://github.com/jlortiz0/PlayerCollars)) mod, with the 1.21.11 additions (consent, `/collar`, Diamond Lock-inator, Inventory Editor) from [minecraftplayz01](https://www.curseforge.com/members/minecraftplayz01)'s [Leashable Collars Fabric Port](https://www.curseforge.com/minecraft/mc-mods/leashable-collars-fabric-port).

## Repository layout

The mod targets several Minecraft versions and mod loaders from **one shared source tree**,
preprocessed per target by [Stonecutter](https://stonecutter.kikugie.dev/). A target is a
*node*, named `<minecraft-version>-<loader>`, and a node contributes nothing but its own
dependency pins:

```text
settings.gradle.kts       declares every node
stonecutter.gradle.kts    active node, loader constants, chiseled tasks
gradle.properties         mod identity shared by every node
gradle/wrapper/           ONE wrapper, Gradle 9.5.1
build.fabric.gradle.kts   per-loader buildscript, selected per node
build.neoforge.gradle.kts
build.forge.gradle.kts
src/main/java/            the source, shared by every node
src/main/resources/       assets, data, and all three loaders' metadata
versions/<node>/gradle.properties    that node's dependency pins, and nothing else
```

Version and loader differences live in Stonecutter conditional comments (`//? if fabric {`,
`//? if >=1.21.6 {`) and, where a difference is wider than a line or two, behind a small seam class
— `Compat`, `Ids`, `Registration`, `Net`, `Events`, `EquippedAccessories`. All nodes share one mod
identity: package `com.dipilodopilasaurus.leashablecollars`, mod id `playercollars`, group
`com.dipilodopilasaurus`.

Only the nodes that are finished are declared by default, so `chiseledBuild` is a real green gate.
Nodes still being migrated are declared under `-Pmigration=all`; see `settings.gradle.kts` for which
is which.

## Building

Build every declared node:

```sh
./gradlew chiseledBuild      # full build, one jar per node
./gradlew chiseledAssemble   # jars only
```

Build one node, or work in it:

```sh
./gradlew :1.21.11-fabric:build
./gradlew "Set active project to 1.21.11-fabric"
```

The working tree is always in the active node's preprocessed form. Let the switch task rewrite it —
hand-editing it into a different node's shape makes the active node compile the wrong branch. Run
`Reset active project` before committing so diffs stay readable.

### Where the jars land

Each node writes to its own `build/libs`:

```text
versions/<minecraft-version>-<loader>/build/libs/
    playercollars-<loader>-<minecraft-version>-<mod.version>.jar
    playercollars-<loader>-<minecraft-version>-<mod.version>-sources.jar
```

for example `versions/1.20.1-fabric/build/libs/playercollars-fabric-1.20.1-1.1.0.jar`. The Minecraft
version is already in the archive name, so it is deliberately *not* repeated as build metadata on
the version; all three buildscripts set `version` to the bare `mod.version`.

To list every release jar after a build:

```sh
find versions -path '*/build/libs/*.jar' ! -name '*-sources.jar'
```

### Migration in progress

Each Minecraft version + loader used to be a fully standalone Gradle build with its own wrapper.
Those modules still sit at `versions/<minecraft-version>/<loader>/`, and each is deleted once its
Stonecutter node is green. They do not collide with the node directories, which are
`versions/<minecraft-version>-<loader>/`. To run a task across the remaining standalone modules:

```sh
./build-all.ps1            # Windows / PowerShell
./build-all.sh             # macOS / Linux / Git Bash
./build-all.ps1 clean build
```

## Versioning

The mod version lives in `mod.version` in `gradle.properties`. Every node reads
it, and `processResources` expands it into all three loaders' metadata (`fabric.mod.json`,
`META-INF/mods.toml`, `META-INF/neoforge.mods.toml`), so bumping the version is a one-line edit:

```properties
mod.version=1.1.0
```

### Which digit to bump

[Semantic Versioning](https://semver.org/), read against what a *player's world* depends on rather
than a Java API. This mod's public surface is its registry ids, the NBT/component tag paths it
writes, its gamerules and config keys, and its network packets.

| Bump | When |
|---|---|
| **PATCH** — `1.1.0` → `1.1.1` | Fixes only. Nothing new to craft, nothing new written to a world. |
| **MINOR** — `1.1.0` → `1.2.0` | New items, blocks, screens, enchantments or gamerules; a new Minecraft version or loader in the matrix. Existing worlds and existing jars are unaffected. |
| **MAJOR** — `1.1.0` → `2.0.0` | Something that shipped before is gone or renamed such as a registry id, a saved tag path, or a config key, so a world from the previous version loads differently, or not at all. |

Adding a target to the build matrix is MINOR, not MAJOR: it takes nothing away from the jars that
already exist.

### Cutting a release

```sh
# 1. bump mod.version in gradle.properties
./gradlew chiseledClean chiseledBuild        # 2. every declared node, from scratch
find versions -path '*/build/libs/*.jar' ! -name '*-sources.jar'   # 3. collect and upload
```
