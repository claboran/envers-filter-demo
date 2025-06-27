package de.laboranowitsch.poc.enversfilterdemo.dto

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Data class representing technical details of a product.
 * This is used to store structured data in the technicalDetailsJson field.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TechnicalDetailsDto(
    val power: String? = null,
    val torque: String? = null,
    // Add other common technical details fields as needed
    // Using a Map for any additional properties not explicitly defined
    val additionalProperties: Map<String, String> = emptyMap()
)
