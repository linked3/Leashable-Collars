# Leashable Collars (Unofficial Port)

This is the GitHub source code repository for my unofficial port of [jlortiz0](https://github.com/jlortiz0)'s [Leashable Collars](https://modrinth.com/mod/leashable-collars) ([PlayerCollars](https://github.com/jlortiz0/PlayerCollars)) mod.

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
