plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains:annotations:23.0.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}