package com.projectronin.event.contract.task

import org.gradle.api.DefaultTask

/**
 * Base task for events that ensures a common group and provides additional helper functions.
 */
abstract class BaseEventTask : DefaultTask() {
    init {
        group = "events"
    }
}
