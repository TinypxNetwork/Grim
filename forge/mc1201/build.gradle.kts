plugins {
    `maven-publish`
    id("net.minecraftforge.gradle")
    grim.`base-conventions`
    id("io.freefair.lombok")
}

val minecraft_version: String by project
val forge_version: String by project

minecraft {
    mappings("official", minecraft_version)
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    compileOnly(project(":common"))
    implementation(project(":forge:shared"))
    compileOnly(libs.packetevents.api)
    compileOnly(libs.cloud.minecraft.modded)
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

publishing.publications.create<MavenPublication>("maven") {
    artifact(tasks["jar"])
}

tasks {
    jar {
        archiveBaseName = "${rootProject.name}-forge-mc1201"
        archiveVersion = rootProject.version as String

        manifest {
            attributes(
                "MixinConfigs" to "grimac.mixins.json"
            )
        }
    }
}
