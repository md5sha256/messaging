rootProject.name = "messaging-parent"
include("client")
findProject(":client")?.name = "messaging-client"
include("common")
findProject(":common")?.name = "messaging-common"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include("server")
findProject(":server")?.name = "messaging-server"
include("client-bootstrap")
findProject(":client-bootstrap")?.name = "messaging-client-bootstrap"
