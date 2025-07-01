package de.laboranowitsch.poc.enversfilterdemo.dto

data class ProductRequestDto(
    val name: String,
    val status: String,
    val technicalDetailsJson: TechnicalDetailsDto? = null,
    val descriptionJson: DescriptionDto? = null,
)
