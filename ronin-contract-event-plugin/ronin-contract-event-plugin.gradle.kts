plugins {
    kotlin("jvm") version "1.8.0"
    `java-gradle-plugin`
    `jacoco`
    `maven-publish`

    id("org.jlleitschuh.gradle.ktlint") version "11.4.0"
    id("com.dipien.releaseshub.gradle.plugin") version "4.0.0"
    id("pl.allegro.tech.build.axion-release") version "1.14.2"
}

repositories {
    maven {
        url = uri("https://repo.devops.projectronin.io/repository/maven-snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
    maven {
        url = uri("https://repo.devops.projectronin.io/repository/maven-releases/")
        mavenContent {
            releasesOnly()
        }
    }
    maven {
        url = uri("https://repo.devops.projectronin.io/repository/maven-public/")
        mavenContent {
            releasesOnly()
        }
    }
    mavenLocal()
    gradlePluginPortal()
}

// Java/Kotlin versioning
java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.json.schema.validator)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.commons.io)
}

jacoco {
    toolVersion = "0.8.8"
    // Custom reports directory can be specfied like this:
    reportsDirectory.set(file("./codecov"))
}

tasks {
    jacocoTestReport {
        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.required.set(true)
        }
    }

    test {
        useJUnitPlatform()

        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStandardStreams = true
            showExceptions = true
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

scmVersion {
    tag {
        initialVersion(pl.allegro.tech.build.axion.release.domain.properties.TagProperties.InitialVersionSupplier { _, _ -> "1.0.0" })
        prefix.set("")
    }
    versionCreator { versionFromTag, position ->
        val supportedHeads = setOf("HEAD", "master", "main")
        if (!supportedHeads.contains(position.branch)) {
            val jiraBranchRegex = Regex("(\\w+)-(\\d+)-(.+)")
            val match = jiraBranchRegex.matchEntire(position.branch)
            val branchExtension = match?.let {
                val (project, number, _) = it.destructured
                "$project$number"
            } ?: position.branch

            "$versionFromTag-$branchExtension"
        } else {
            versionFromTag
        }
    }
}

version = scmVersion.version

gradlePlugin {
    plugins {
        create("eventContractPlugin") {
            id = "com.projectronin.event.contract"
            implementationClass = "com.projectronin.event.contract.EventContractPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "nexus"
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_TOKEN")
            }
            url = if (project.version.toString().endsWith("SNAPSHOT")) {
                uri("https://repo.devops.projectronin.io/repository/maven-snapshots/")
            } else {
                uri("https://repo.devops.projectronin.io/repository/maven-releases/")
            }
        }
    }
}
