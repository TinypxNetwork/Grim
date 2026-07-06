import versioning.BuildConfig

plugins {
    `maven-publish`
    grim.`base-conventions`
    grim.`jij-conventions`
}

repositories {
    if (BuildConfig.mavenLocalOverride) mavenLocal()
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(project(":forge:shared"))
    implementation(project(":forge:mc1201"))
    implementation(libs.packetevents.forge)
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