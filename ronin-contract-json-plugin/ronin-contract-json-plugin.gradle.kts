import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("jvm") version "1.8.0"
    `java-gradle-plugin`
    `maven-publish`

    alias(libs.plugins.ktlint)
    alias(libs.plugins.releasehub)
    alias(libs.plugins.axion.release)
    alias(libs.plugins.kover)
}

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
    implementation(libs.jsonschematopojo)
    implementation(libs.axion.release)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.commons.io)
    testImplementation(libs.assertj)
    testImplementation(gradleTestKit())
    testImplementation(libs.testcontainers)
    testImplementation(libs.okhttp)
}

tasks {
    test {
        useJUnitPlatform()

        testLogging {
            events(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED)
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}

scmVersion {
    tag {
        initialVersion { _, _ -> "1.0.0" }
        prefix.set("")
    }
    versionCreator { versionFromTag, position ->
        val supportedHeads = setOf("master", "main")
        val branchName = System.getenv("REF_NAME")?.ifBlank { null } ?: position.branch
        if (!supportedHeads.contains(branchName)) {
            val jiraBranchRegex = Regex("(?:.*/)?(\\w+)-(\\d+)-(.+)")
            val match = jiraBranchRegex.matchEntire(branchName)
            val branchExtension = match?.let {
                val (project, number, _) = it.destructured
                "$project$number"
            } ?: branchName

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
            id = "com.projectronin.json.contract"
            implementationClass = "com.projectronin.json.contract.JsonContractPlugin"
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

tasks.register<Copy>("copyInitializationFiles") {
    from(layout.projectDirectory.dir("src/main/initializer"))
    into(layout.buildDirectory.dir("initializer"))
    filter(
        ReplaceTokens::class,
        "tokens" to mapOf(
            "projectVersion" to project.version
        )
    )
}

tasks.getByName("build").dependsOn("copyInitializationFiles")
