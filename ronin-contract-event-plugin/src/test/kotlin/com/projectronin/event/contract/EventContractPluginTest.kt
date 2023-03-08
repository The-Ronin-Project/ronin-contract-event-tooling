package com.projectronin.event.contract

import com.projectronin.event.contract.task.CleanTask
import com.projectronin.event.contract.task.DocumentationTask
import com.projectronin.event.contract.task.TestTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.junit.jupiter.api.Test

class EventContractPluginTest {
    private val mockExtensions = mockk<ExtensionContainer>(relaxed = true)
    private val mockTasks = mockk<TaskContainer>(relaxed = true) {
        every { register(any(), any()) } returns mockk()
        every { getByName(any(), any<Action<Task>>()) } returns mockk()
    }
    private val project = mockk<Project>(relaxed = true) {
        every { extensions } returns mockExtensions
        every { tasks } returns mockTasks
    }
    private val plugin = EventContractPlugin()

    @Test
    fun `registers extension`() {
        plugin.apply(project)

        verify(exactly = 1) {
            mockExtensions.create(
                EventContractExtension.NAME,
                EventContractExtension::class.java
            )
        }
    }

    @Test
    fun `registers cleanEvents task`() {
        plugin.apply(project)

        verify(exactly = 1) {
            mockTasks.register("cleanEvents", CleanTask::class.java)
        }
    }

    @Test
    fun `associates cleanEvents task with clean`() {
        val cleanEventTask = mockk<TaskProvider<CleanTask>>()
        every { mockTasks.register("cleanEvents", CleanTask::class.java) } returns cleanEventTask

        val actionSlot = slot<Action<Task>>()
        every { mockTasks.getByName("clean", capture(actionSlot)) } returns mockk()

        plugin.apply(project)

        val task = mockk<Task>(relaxed = true)
        actionSlot.captured.execute(task)

        verify(exactly = 1) { task.dependsOn(cleanEventTask) }
    }

    @Test
    fun `registers testEvents task`() {
        plugin.apply(project)

        verify(exactly = 1) {
            mockTasks.register("testEvents", TestTask::class.java)
        }
    }

    @Test
    fun `associates testEvents task with check`() {
        val testEventsTask = mockk<TaskProvider<TestTask>>()
        every { mockTasks.register("testEvents", TestTask::class.java) } returns testEventsTask

        val actionSlot = slot<Action<Task>>()
        every { mockTasks.getByName("check", capture(actionSlot)) } returns mockk()

        plugin.apply(project)

        val task = mockk<Task>(relaxed = true)
        actionSlot.captured.execute(task)

        verify(exactly = 1) { task.dependsOn(testEventsTask) }
    }

    @Test
    fun `registers generateEventDocs task`() {
        plugin.apply(project)

        verify(exactly = 1) {
            mockTasks.register("generateEventDocs", DocumentationTask::class.java)
        }
    }
}
