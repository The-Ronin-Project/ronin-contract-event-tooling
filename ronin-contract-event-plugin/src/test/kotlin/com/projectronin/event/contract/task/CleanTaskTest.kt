package com.projectronin.event.contract.task

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class CleanTaskTest {
    private fun getTask(makeDocsFolder: Boolean, vararg versions: Int): CleanTask {
        val project =
            ProjectBuilder.builder()
                .build()

        versions.forEach {
            val versionDirectory = "${project.projectDir}/v$it/"
            File(versionDirectory).mkdirs()

            if (makeDocsFolder) {
                File("$versionDirectory/docs/").mkdirs()
            }
        }

        return project.tasks.register("cleanTask", CleanTask::class.java).get()
    }

    private fun getDocDirectory(task: CleanTask, version: Int) = File("${task.project.projectDir}/v$version/docs/")

    private fun addDocs(directory: File, count: Int) {
        (1..count).forEach {
            File("$directory/page$it.html").createNewFile()
        }
    }

    private fun getDocs(task: CleanTask): List<String> =
        task.project.projectDir.listFiles { f -> f.name.matches(Regex("v\\d+")) }
            .flatMap {
                it.listFiles().singleOrNull { it.name == "docs" }?.walk()?.map { it.name }?.toList() ?: emptyList()
            }

    @Test
    fun `cleans when no version directories`() {
        val task = getTask(false)
        task.clean()

        assertEquals(0, getDocs(task).size)
    }

    @Test
    fun `cleans single version with no docs`() {
        val task = getTask(false, 1)
        task.clean()

        assertEquals(0, getDocs(task).size)
    }

    @Test
    fun `cleans single version with docs`() {
        val task = getTask(true, 1)
        val v1DocDir = getDocDirectory(task, 1)
        addDocs(v1DocDir, 2)
        assertEquals(3, getDocs(task).size) // new docs + itself

        task.clean()

        assertEquals(0, getDocs(task).size)
    }

    @Test
    fun `cleans single version with nested docs`() {
        val task = getTask(true, 1)
        val v1DocDir = getDocDirectory(task, 1)
        addDocs(v1DocDir, 2)

        val subDir = File("$v1DocDir/sub")
        subDir.mkdirs()
        addDocs(subDir, 3)

        assertEquals(7, getDocs(task).size) // doc + 2 docs + subDir + 3 docs

        task.clean()

        assertEquals(0, getDocs(task).size)
    }

    @Test
    fun `cleans multiple versions`() {
        val task = getTask(true, 1, 2, 3)

        val v1DocDir = getDocDirectory(task, 1)
        addDocs(v1DocDir, 2)

        val v2DocDir = getDocDirectory(task, 2)
        addDocs(v2DocDir, 2)

        val v3DocDir = getDocDirectory(task, 3)
        addDocs(v3DocDir, 2)

        assertEquals(9, getDocs(task).size) // (doc + 2 docs) x 3

        task.clean()

        assertEquals(0, getDocs(task).size)
    }
}
