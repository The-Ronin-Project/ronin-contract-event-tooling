package com.projectronin.event.contract.task

import org.gradle.api.tasks.TaskAction

/**
 * Cleans up artifacts created by other tasks within this plugin.
 */
open class CleanTask : BaseEventTask() {
    @TaskAction
    fun clean() {
        val versionDirectories = getVersionDirectories()
        versionDirectories.forEach { version ->
            val docs = version.listFiles().singleOrNull { f -> f.name == "docs" }
            docs?.let { it.deleteRecursively() }
        }
    }
}
