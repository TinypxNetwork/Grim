import versioning.BuildConfig

plugins {
    `maven-publish`
    grim.`base-conventions`
}

repositories {
    if (BuildConfig.mavenLocalOverride) mavenLocal()
    mavenLocal()
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.grim.ac/public/releases")
    maven("https://maven.grim.ac/public/snapshots") {
        mavenContent { snapshotsOnly() }
    }
    maven("https://repo.grim.ac/snapshots")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.scarsz.me/content/repositories/releases")
    mavenCentral()
}

subprojects {
    repositories {
        if (BuildConfig.mavenLocalOverride) mavenLocal()
        mavenLocal()
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.grim.ac/public/releases")
        maven("https://maven.grim.ac/public/snapshots") {
            mavenContent { snapshotsOnly() }
        }
        maven("https://repo.grim.ac/snapshots")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://nexus.scarsz.me/content/repositories/releases")
        mavenCentral()
    }
}

dependencies {
    implementation(project(":forge:shared"))
    implementation(project(":forge:mc1201"))
}

publishing.publications.create<MavenPublication>("maven") {
    artifact(tasks["jar"])
}

tasks {
    jar {
        archiveBaseName = "${rootProject.name}-forge"
        archiveVersion = rootProject.version as String
    }
}
