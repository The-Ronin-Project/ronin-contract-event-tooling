package com.projectronin.event.contract.task

import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Task responsible for generating documentation for Event contracts.
 */
open class DocumentationTask @Inject constructor(private val execOperations: ExecOperations) : BaseEventTask() {
    @TaskAction
    fun generateDocumentation() {
        DocumentationGenerator(execOperations, project).generate()
    }
}

/**
 * Logic for generating documentation. This is separated out to allow for mock testing.
 */
class DocumentationGenerator(private val execOperations: ExecOperations, private val project: Project) {
    fun generate() {
        execOperations.exec {
            it.workingDir(project.projectDir)
            it.commandLine(
                listOf(
                    "docker", "run", "--rm", "-u", "root:root",
                    "-v", "${project.projectDir}:/app",
                    "--pull", "always",
                    "docker-repo.devops.projectronin.io/ronin-contract-event-tooling:v1",
                    "contract-tools", "doc"
                )
            )
        }
    }
}
