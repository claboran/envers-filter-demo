package de.laboranowitsch.poc.enversfilterdemo.dto

data class ProductRequestDto(
    val name: String,
    val status: String,
    val technicalDetailsJson: String, // e.g., {"weight": "10kg", "power": "220V"}
    val descriptionJson: String     // e.g., {"de": "Beschreibung", "en": "Description"}
)
