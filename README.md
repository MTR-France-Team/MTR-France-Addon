# MTR France Addon v2

[![Minecraft](https://img.shields.io/badge/Minecraft-1.18.2--1.20.4-green.svg)](https://minecraft.net/)
[![MTR](https://img.shields.io/badge/MTR-4.0.0+-blue.svg)](https://minecrafttransitrailway.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE.txt)

[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_64h.png)](https://github.com/MTR-France-Team/MTR-France-Addon)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_64h.png)](https://modrinth.com/project/YJct9p8I)

![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/fabric_46h.png)
![forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/supported/forge_46h.png)

French railway content for MTR.

By Team MTR-FRA. (Internal package/project name is `mtrfra` / "MTRFRA" — the mod id itself stays `mtrfranceaddon` for save/resourcepack continuity with the original release.)

## Supported versions

| Minecraft | Fabric | Forge | MTR API |
|-----------|:------:|:-----:|---------|
| 1.16.5 (dropped) | ❌ | ❌ | 4.0.x (`org.mtr.mapping`) |
| 1.17.1 (dropped) | ❌ | ❌ | 4.0.x (`org.mtr.mapping`) |
| 1.18.2 (STS) | ✅ (here) | ✅ (`legacy-forge/`) | 4.0.x (`org.mtr.mapping`) |
| 1.19.2 (STS) | ✅ (here) | ✅ (`legacy-forge/`) | 4.0.x (`org.mtr.mapping`) |
| 1.19.4 (STS) | ✅ (here) | ✅ (`legacy-forge/`) | 4.0.x (`org.mtr.mapping`) |
| 1.20.1    | ✅ (here) | ✅ (`legacy-forge/`) | 4.0.x (`org.mtr.mapping`) |
| 1.20.4    | ✅ (here) | ✅ (`legacy-forge/`) | 4.0.x (`org.mtr.mapping`) |
| 1.21.x (upcoming) | 🔜 | 🔜 (NeoForge, not Forge) | 4.1.x (pending stable) |

1.16.5 and 1.17.1 were supported by the original pre-rewrite addon but are not carried into this tree (too low demand to justify the mapping/API work this far back). 1.18.x and 1.19.x are short-term support (a handful of players remain on them) and will be dropped once they migrate. Forge support for 1.18.x-1.20.x lives in the nested [`legacy-forge/`](legacy-forge/) project, on its own Gradle 8 wrapper — see [Repository layout](#repository-layout) for why it can't share this build. 1.21.x support (Fabric + NeoForge) is planned but currently shelved pending a stable MTR 4.1 release.

### Java / JDK requirements

Mojang itself raises the minimum Java version per Minecraft release, independently of anything this project decides:

| Minecraft | Mojang's minimum JDK | This project |
|-----------|:---------------------:|---------------|
| 1.16.5 / 1.17.1 | 8 / 16 | — (dropped, see above) |
| 1.18.2 – 1.20.4 | 17 | **17** (Fabric toolchains) |
| 1.21.x (upcoming) | 21 | 21 (planned) |

We target JDK 17 rather than something lower for one simple reason: **1.18.2 is our oldest supported version, and 1.18.2 already requires Java 17 per Mojang.** There's no version left in this tree that Java 8 or 16 would actually help — those only mattered for 1.16.5/1.17.1, which are dropped. So 17 isn't a compromise, it's just the real floor of what we ship.

The one extra wrinkle: Stonecutter itself (the tool that manages the multi-version `src/` tree, not the compiled mod) needs **JDK 21+ on `JAVA_HOME`** to run, regardless of which Minecraft version you're actively building — see [Building](#building) below. That's a build-tool requirement, separate from the per-version Java 17 toolchain the compiled code actually targets.

## Repository layout

This repo is a [Stonecutter](https://stonecutter.kikugie.dev/) multi-version project: `src/main/java` and `src/main/resources` hold **one** shared source tree for every Fabric version above. Each Minecraft version is its own Gradle subproject under `versions/<name>/`, holding only that version's dependency pins (`gradle.properties`) — never source code. Stonecutter "chisels" the shared source into each subproject at build time using comment directives (`//? if ... { ... //? }`), so the checked-out source is always valid, fully-typed Java.

**Forge isn't part of this Stonecutter tree.** Stonecutter requires Gradle 9+, and ForgeGradle explicitly refuses to run under Gradle 9 — one Gradle invocation can only use one Gradle version, so Forge can't share this build. It lives instead in the nested [`legacy-forge/`](legacy-forge/) project, on its own Gradle 8 wrapper, with a small hand-written Gradle task that resolves the same `//? if` comments Stonecutter uses here before every compile. See [`legacy-forge/README.md`](legacy-forge/README.md) for details.

## Building

Requires **JDK 21+** on `JAVA_HOME` for this Stonecutter tree (its own runtime requirement; per-version Java toolchains for compiling are 17), and a **JDK 17** ForgeGradle accepts for `legacy-forge/`.

```bash
./gradlew build                    # every Fabric/NeoForge version in this tree
./gradlew :1.20.4-fabric:build      # just one version

cd legacy-forge && ./gradlew build -Pminecraft_version=1.20.4   # Forge, one version
```

Or use the top-level script, which drives both:

```bash
./build.sh                    # every version, every loader (Fabric + Forge)
./build.sh -v 1.20.4           # one Minecraft version, all its loaders
./build.sh -l forge            # Forge only, every version
```

Jars land in `releases/`.

## Developing

`stonecutter active "1.20.4-fabric"` in `stonecutter.gradle.kts` controls which version the root project (and your IDE) currently represents — edit `src/` directly against that version. To switch:

```bash
./gradlew "Set active project to 1.20.1-fabric"   # rewrites src/ comments and updates `stonecutter active`
./gradlew "Reset active project"                   # back to the vcsVersion (1.20.4-fabric) - do this before committing
./gradlew "Refresh active project"                  # re-run the comment processor without switching, if a `//? if` block looks out of sync
./gradlew stonecutterIdea                           # generates IntelliJ run configurations for the tasks above
```

Never hand-edit which side of a `//? if / else` block is commented out — always go through one of the tasks above, so the branch that's live in your working copy actually matches the active version's constants.

## License

MIT — see [LICENSE.txt](LICENSE.txt).
