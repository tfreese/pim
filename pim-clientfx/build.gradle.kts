plugins {
    id("java-library")
    id("org.springframework.boot")
    id("org.openjfx.javafxplugin")
}

description = "GUI for PIM"

// For JavaFx native-Library Downloads.
//configurations.matching { it.isCanBeResolved }.configureEach {
//    attributes {
//        attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>(Usage.JAVA_RUNTIME))
//        attribute(
//            OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE,
//            objects.named<OperatingSystemFamily>(OperatingSystemFamily.LINUX)
//        )
//        attribute(
//            MachineArchitecture.ARCHITECTURE_ATTRIBUTE,
//            objects.named<MachineArchitecture>(MachineArchitecture.X86_64)
//        )
//    }
//}

javafx {
    version = property("version_javafx").toString()
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
    configuration = "implementation"
    setPlatform("linux") // linux, windows, mac
    // sdk = "PATH"
}

dependencies {
    implementation(project(":pim-core"))
    implementation(project(":pim-server"))
    implementation("org.springframework.boot:spring-boot-starter-restclient")
}

tasks.named<Test>("test") {
    isEnabled = false
}

// Start: gradle bootRun --args="--spring.profiles.active=dev"
// The archive name. If the name has not been explicitly set, the pattern for the name is:
// [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]
// archiveFileName = "test2-boot.jar"
springBoot {
    mainClass = "de.freese.pim.gui.PimClientLauncher"
}

// gradle bootRun --args="--spring.profiles.active=ClientStandalone"
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    args = listOf(
        "--spring.profiles.active=ClientRest"
    )
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    dependsOn(project(":pim-server").tasks.named("build"))
}

tasks.named<ProcessResources>("processResources") {
    val map = mapOf(
        "project_description" to project.description,
        "project_artifactId" to project.name,
        "project_version" to project.version
    )

    filesMatching("application.properties") {
        filter(
            mapOf("tokens" to map),
            org.apache.tools.ant.filters.ReplaceTokens::class.java
        )
    }
}
