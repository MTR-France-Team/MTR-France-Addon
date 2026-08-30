plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.17.14" apply false
    id("net.neoforged.moddev") version "2.0.142" apply false
}

stonecutter active "1.20.4-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
}
