# MTR France Addon v2 — Forge (legacy)

Forge builds of [MTR France Addon v2](..) for Minecraft **1.18.2**, **1.19.2**, **1.19.4**, **1.20.1** and **1.20.4** (MTR 4.0.x, `org.mtr.mapping` API).

## Why this is a separate project

The main [MTRFRA](../README.md) tree uses [Stonecutter](https://stonecutter.kikugie.dev/) to share one source tree across every Fabric/NeoForge version, and Stonecutter requires **Gradle 9+**. ForgeGradle (version range `[6.0.16,6.2)`) explicitly refuses to run under Gradle 9. A single Gradle invocation can only use one Gradle version, so Forge lives here instead, on its own **Gradle 8** wrapper.

## How the shared source gets here

The shared mod code under `../src/main/java/fr/mtrfra/mod` uses stonecutter `//? if >=X.Y.Z { ... } else { /* ... */ }` comments for the handful of places where the Mojang/Forge API actually changed across this version range (`GuiGraphics` vs `PoseStack`, `org.joml.Quaternionf` vs the older math classes, the creative-tab rework, etc). Stonecutter resolves those automatically on the Fabric/NeoForge side; this project has no such preprocessor, so a Gradle task (`resolveStonecutter`, in `build.gradle`) does the same resolution by hand before every compile: it reads `../src/main/java/fr/mtrfra/mod`, keeps whichever branch matches `minecraft_version`, and writes plain Java into `src/main/java/fr/mtrfra/mod` here.

That generated tree is **not committed** (see `.gitignore`) — it's regenerated fresh on every build from whatever `minecraft_version` is set to. Never hand-edit files under `src/main/java/fr/mtrfra/mod/`; edit the shared source in `../src/main/java/fr/mtrfra/mod` instead.

`src/main/java/fr/mtrfra/forge/MainForge.java` is the one file that lives only here — the Forge entrypoint, hand-written for this loader, untouched by the resolver.

## Building

Requires **JDK 17**.

```bash
./gradlew build              # builds whichever minecraft_version is set in gradle.properties
```

To target another version, edit `minecraft_version` in `gradle.properties`, or pass it on the command line without touching the file:

```bash
./gradlew build -Pminecraft_version=1.18.2
```

`../build.sh -l forge` does this for every version in one pass.

## License

MIT — see [../LICENSE.txt](../LICENSE.txt).
