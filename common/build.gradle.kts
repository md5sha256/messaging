plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains:annotations:24.0.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}