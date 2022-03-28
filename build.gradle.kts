plugins {
    java
    idea
}

group = "io.github.md5sha256.messaging"
version = "1.0.0-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

subprojects {
    group = rootProject.group
    version = rootProject.version

    apply {
        plugin<JavaPlugin>()
        plugin<IdeaPlugin>()
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    tasks {

        withType(Test::class) {
            useJUnitPlatform()
        }

        withType(JavaCompile::class) {
            options.release.set(17)
            options.encoding = Charsets.UTF_8.name()
            options.isDeprecation = true
        }

    }

}
