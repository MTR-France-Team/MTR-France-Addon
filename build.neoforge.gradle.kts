plugins {
    id("net.neoforged.moddev")
    id("com.gradleup.shadow") version "8.3.4"
}

base.archivesName = "${property("mod.id")}-neoforge"
version = "${property("mod.version")}+${sc.current.version}"
group = "fr.mtrfra"

repositories {
    maven("https://repo.essential.gg/repository/maven-public")
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") }
        filter { includeGroup("maven.modrinth") }
    }
}

neoForge {
    version = property("dependency.neoforge") as String

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets["main"])
        }
    }

    runs {
        create("client") {
            client()
            gameDirectory.set(layout.projectDirectory.dir("../../run-shared"))
            programArgument("--username")
            programArgument("Acaretia")
            programArgument("--uuid")
            programArgument("e2f2d262-39a8-4281-a33a-52c41476a2b6")
        }
        create("server") {
            server()
            gameDirectory.set(layout.projectDirectory.dir("../../run-shared"))
        }
    }
}

configurations {
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

dependencies {
    implementation("maven.modrinth:minecraft-transit-railway:NEOFORGE-${property("dependency.mtr")}+${sc.current.version}")

    if (sc.current.version.startsWith("1.21")) {
        implementation("gg.essential:elementa:${property("dependency.elementa")}")
        "shadowBundle"("gg.essential:elementa:${property("dependency.elementa")}")
        implementation("gg.essential:universalcraft-${property("dependency.universal_craft_minecraft")}-neoforge:${property("dependency.universal_craft")}")
        "shadowBundle"("gg.essential:universalcraft-${property("dependency.universal_craft_minecraft")}-neoforge:${property("dependency.universal_craft")}")
    }
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modDescription = property("mod.description").toString()
val modLicense = property("mod.license").toString()
val modAuthor = property("mod.author").toString()
val modIssues = property("mod.issues").toString()
val packFormat = property("pack_format").toString()

tasks.processResources {
    inputs.property("version", version)
    inputs.property("minecraft_version", sc.current.version)
    inputs.property("pack_format", packFormat)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(
            "version" to version,
            "minecraft_version" to sc.current.version,
            "mod_id" to modId,
            "mod_name" to modName,
            "mod_description" to modDescription,
            "mod_license" to modLicense,
            "mod_author" to modAuthor,
            "mod_issues" to modIssues
        )
    }

    filesMatching("pack.mcmeta") {
        expand("pack_format" to packFormat)
    }

    exclude("fabric.mod.json", "META-INF/mods.toml")
}

tasks.jar {
    from(rootProject.file("LICENSE.txt")) {
        rename { "${it}_${base.archivesName.get()}" }
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    configurations = listOf(project.configurations["shadowBundle"])
}

tasks.named("assemble") {
    dependsOn(tasks.shadowJar)
}
