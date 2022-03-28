plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.12"
}

version = "1.0.0-SNAPSHOT"

dependencies {
    implementation("org.fxmisc.flowless:flowless:0.6.9")
    implementation(projects.messagingCommon)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

javafx {
    version = "17.0.1"
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("io.github.md5sha256.Main")
}
