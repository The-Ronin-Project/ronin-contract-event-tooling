package com.projectronin.event.contract

import com.projectronin.event.contract.task.CleanTask
import com.projectronin.event.contract.task.DocumentationTask
import com.projectronin.event.contract.task.TestTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The EventContractPlugin provides access to a set of tasks capable of validating and generating documentation for schemas.
 */
class EventContractPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EventContractExtension.NAME, EventContractExtension::class.java)

        val cleanPlugin = project.tasks.register("cleanEvents", CleanTask::class.java)
        project.tasks.getByName("clean") {
            it.dependsOn(cleanPlugin)
        }

        val testTask = project.tasks.register("testEvents", TestTask::class.java)
        project.tasks.getByName("check") {
            it.dependsOn(testTask)
        }

        project.tasks.register("generateEventDocs", DocumentationTask::class.java)
    }
}
