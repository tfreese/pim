// Execute Tasks in SubModule: gradle MODUL:clean build
plugins {
    id("de.freese.gradle.conventions").apply(false)
    // id("io.spring.dependency-management").apply(false)
    id("org.openjfx.javafxplugin").apply(false)
    id("org.springframework.boot").apply(false)
}

allprojects {
    plugins.apply("base")

    tasks.named<Delete>("clean") {
        delete(layout.projectDirectory.dir("bin"))
        delete("logs")
        delete("out")
        delete("target")
    }
}

subprojects {
    plugins.apply("de.freese.gradle.conventions")
    plugins.apply("io.spring.dependency-management")

    extensions.configure(io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension::class.java) {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:" + property("version_springBoot"))
        }

        dependencies {
            dependency("commons-cli:commons-cli:" + property("version_commonsCli"))
            dependency("org.jsoup:jsoup:" + property("version_jsoup"))
        }
    }

    plugins.withType<JavaPlugin> {
        dependencies {
            add("runtimeOnly", "org.springframework.boot:spring-boot-properties-migrator")

            add("testImplementation", "org.awaitility:awaitility")
            add("testImplementation", "org.junit.jupiter:junit-jupiter")

            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
        }
    }
}
