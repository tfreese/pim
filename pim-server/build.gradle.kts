plugins {
    id("java-library")
    id("org.springframework.boot")
}

description = "Server for PIM"

dependencies {
    implementation(project(":pim-core"))

    api("org.springframework.boot:spring-boot-starter-webmvc")

    runtimeOnly("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

// Start: gradle bootRun --args="--spring.profiles.active=dev"
// The archive name. If the name has not been explicitly set, the pattern for the name is:
// [archiveBaseName]-[archiveAppendix]-[archiveVersion]-[archiveClassifier].[archiveExtension]
// archiveFileName = "my-boot.jar"
springBoot {
    mainClass = "de.freese.pim.server.PimServerApplication"
}

// gradle bootRun --args="--spring.profiles.active=Server,HsqldbEmbeddedServer --server.port=65111"
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    args = listOf(
        "--spring.profiles.active=Server,HsqldbEmbeddedServer", "--server.port=65111"
    )
}

// For Placeholder in application.properties/application.yml
val artifactId = project.name

tasks.named<ProcessResources>("processResources") {
    val map = mapOf(
        "project_description" to project.description,
        "project_artifactId" to project.name,
        "project_version" to project.version
    )

    // , "pim-server_banner.txt"
    filesMatching("application-Server.properties") {
        // During Problems escape Placeholder: \${...}
        // expand(project.properties)
        // expand(map)

        filter(
            mapOf("tokens" to map),
            org.apache.tools.ant.filters.ReplaceTokens::class.java
        )
    }
}
