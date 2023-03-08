package com.projectronin.event.contract.task

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import org.junit.jupiter.api.Test
import java.io.File

class DocumentationGeneratorTest {
    private val execOperations = mockk<ExecOperations>()
    private val projectDirFile = File("project-directory")
    private val project = mockk<Project> {
        every { projectDir } returns projectDirFile
    }
    private val generator = DocumentationGenerator(execOperations, project)

    @Test
    fun `uses current project directory`() {
        val execSlot = slot<Action<ExecSpec>>()
        every { execOperations.exec(capture(execSlot)) } returns mockk()

        generator.generate()

        val execSpec = mockk<ExecSpec>(relaxed = true)
        execSlot.captured.execute(execSpec)

        verify { execSpec.workingDir(projectDirFile) }
    }

    @Test
    fun `uses docker`() {
        val execSlot = slot<Action<ExecSpec>>()
        every { execOperations.exec(capture(execSlot)) } returns mockk()

        generator.generate()

        val execSpec = mockk<ExecSpec>(relaxed = true)
        execSlot.captured.execute(execSpec)

        verify {
            execSpec.commandLine(
                listOf(
                    "docker",
                    "run",
                    "--rm",
                    "-u",
                    "root:root",
                    "-v",
                    "project-directory:/app",
                    "--pull",
                    "always",
                    "docker-repo.devops.projectronin.io/ronin-contract-event-tooling:v1",
                    "contract-tools",
                    "doc"
                )
            )
        }
    }
}
