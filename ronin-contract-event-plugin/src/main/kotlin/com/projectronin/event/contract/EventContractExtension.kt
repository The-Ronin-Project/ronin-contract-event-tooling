package com.projectronin.event.contract

import com.networknt.schema.SpecVersion.VersionFlag
import org.gradle.api.Project

/**
 * Extension defining the configuration for the [EventContractPlugin]
 */
open class EventContractExtension {
    companion object {
        const val NAME = "events"
    }

    /**
     * The JSON Schema version against which this contract should be evaluated. Defaults to V201909.
     */
    var specVersion: VersionFlag = VersionFlag.V201909

    /**
     * List of keywords that should be ignored while validating the event contracts. This may help ensure that validation
     * errors or warnings are not produced for items that may be necessary in the schema for generation or other processing.
     */
    var ignoredValidationKeywords: List<String> = emptyList()
}

/**
 * Helper function for retrieving this configuration extension.
 */
internal fun Project.config(): EventContractExtension =
    extensions.getByName(EventContractExtension.NAME) as EventContractExtension
