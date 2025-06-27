package de.laboranowitsch.poc.enversfilterdemo.dto

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Data class representing multilingual descriptions of a product.
 * This is used to store structured data in the descriptionJson field.
 * The keys are language codes (e.g., "en", "de") and the values are the descriptions in those languages.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class DescriptionDto(
    val descriptions: Map<String, String> = emptyMap()
)
