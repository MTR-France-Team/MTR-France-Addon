import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("fabric-loom")
}

base.archivesName = "${property("mod.id")}-fabric"
version = "${property("mod.version")}+${sc.current.version}"
group = "fr.mtrfra"

loom {
    runs {
        named("client") {
            runDir("../../run-shared")
            programArg("--username")
            programArg("Acaretia")
            programArg("--uuid")
            programArg("e2f2d262-39a8-4281-a33a-52c41476a2b6")
        }
        named("server") {
            runDir("../../run-shared")
        }
    }
}

val isMtr41Generation = sc.current.version.startsWith("1.21")
val mtrDependencyRange = if (isMtr41Generation) ">=4.1.0-alpha" else ">=4.0.4 <4.1"

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.nucleoid.xyz/")
    maven("https://repo.essential.gg/repository/maven-public")
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("dependency.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("dependency.fabric_api")}")
    modImplementation("maven.modrinth:minecraft-transit-railway:FABRIC-${property("dependency.mtr")}+${sc.current.version}")
    modImplementation("com.terraformersmc:modmenu:${property("dependency.mod_menu")}")

    if (sc.current.version.startsWith("1.21")) {
        implementation("gg.essential:elementa:${property("dependency.elementa")}")
        include("gg.essential:elementa:${property("dependency.elementa")}")
        modImplementation("gg.essential:universalcraft-${property("dependency.universal_craft_minecraft")}-fabric:${property("dependency.universal_craft")}")
        include("gg.essential:universalcraft-${property("dependency.universal_craft_minecraft")}-fabric:${property("dependency.universal_craft")}")
    }
}

java {
    withSourcesJar()
    val javaVersion = if (sc.current.version.startsWith("1.21")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modDescription = property("mod.description").toString()
val modLicense = property("mod.license").toString()
val modHomepage = property("mod.homepage").toString()
val modSources = property("mod.sources").toString()
val modIssues = property("mod.issues").toString()
val packFormat = property("pack_format").toString()

tasks.processResources {
    inputs.property("version", version)
    inputs.property("minecraft_version", sc.current.version)
    inputs.property("pack_format", packFormat)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "minecraft_version" to sc.current.version,
            "mod_id" to modId,
            "mod_name" to modName,
            "mod_description" to modDescription,
            "mod_license" to modLicense,
            "mod_homepage" to modHomepage,
            "mod_sources" to modSources,
            "mod_issues" to modIssues,
            "mtr_dependency" to mtrDependencyRange
        )
    }

    filesMatching("pack.mcmeta") {
        expand("pack_format" to packFormat)
    }

    exclude("META-INF/mods.toml", "META-INF/neoforge.mods.toml")
}

tasks.jar {
    from(rootProject.file("LICENSE.txt")) {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

tasks.withType<RemapJarTask>().configureEach {
    archiveClassifier.set("")
}
