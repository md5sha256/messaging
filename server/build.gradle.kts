plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.messagingCommon)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}