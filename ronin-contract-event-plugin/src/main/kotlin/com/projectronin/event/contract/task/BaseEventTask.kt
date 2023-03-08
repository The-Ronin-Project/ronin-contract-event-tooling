package com.projectronin.event.contract.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import java.io.File

/**
 * Base task for events that ensures a common group and provides additional helper functions.
 */
abstract class BaseEventTask : DefaultTask() {
    init {
        group = "events"
    }

    @Internal
    protected fun getVersionDirectories(): List<File> =
        project.projectDir.listFiles { f -> f.name.matches(Regex("v\\d+")) }.toList()
}
