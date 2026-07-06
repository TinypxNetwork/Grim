plugins {
    `java-library`
    id("net.minecraftforge.gradle")
    grim.`base-conventions`
}

repositories {
    mavenCentral()
}

val minecraft_version: String by project
val forge_version: String by project

minecraft {
    mappings("official", minecraft_version)
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    compileOnly(project(":common"))
    compileOnly(libs.grim.api)
    compileOnly(libs.grim.internal)
    compileOnly(libs.grim.internal.shims)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.cloud.core)
    compileOnly(libs.cloud.minecraft.modded)
    compileOnly(libs.luckperms)
    compileOnly(libs.packetevents.api)
    compileOnly(libs.placeholderapi.forge)
    compileOnly(libs.netty)
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.text.serializer.gson)
}
