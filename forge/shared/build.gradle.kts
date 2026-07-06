plugins {
    `java-library`
    grim.`base-conventions`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(libs.grim.api)
    compileOnly(libs.grim.internal)
    compileOnly(libs.grim.internal.shims)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.cloud.core)
    compileOnly(libs.luckperms)
    compileOnly(libs.packetevents.api)
    compileOnly(libs.placeholderapi.forge)
}