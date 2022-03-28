import io.github.md5sha256.messaging.bootstrapper.BootstrapPlugin


apply {
    plugin<BootstrapPlugin>()
}

dependencies {
    runtimeOnly(projects.messagingClient)
}

tasks {

    withType(Jar::class) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(configurations.runtimeClasspath)
        manifest {
            attributes["Main-Class"] = "io.github.md5sha256.messaging.Bootstrap"
        }
    }

    withType(ProcessResources::class) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets["main"].resources)
        from(project(":messaging-client").tasks.jar.get().archiveFile)
        filesMatching("bootstrap.properties") {
            expand(
                    "version" to project.version,
                    "mainClass" to "io.github.md5sha256.messaging.client.Client"
            )
        }
    }

}

