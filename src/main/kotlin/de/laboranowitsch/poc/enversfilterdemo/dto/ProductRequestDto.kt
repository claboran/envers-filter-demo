package de.laboranowitsch.poc.enversfilterdemo.dto

data class ProductRequestDto(
    val name: String,
    val status: String,
    val technicalDetailsJson: TechnicalDetailsDto,
    val descriptionJson: DescriptionDto
)
