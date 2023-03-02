plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains:annotations:24.0.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}