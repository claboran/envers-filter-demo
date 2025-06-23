package de.laboranowitsch.poc.enversfilterdemo.dto

import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import java.time.Instant

data class ProductRevisionDto(
    val revisionNumber: Number,
    val revisionTimestamp: Instant,
    val product: ParentEntity // Shows the state of the parent at that revision
)
