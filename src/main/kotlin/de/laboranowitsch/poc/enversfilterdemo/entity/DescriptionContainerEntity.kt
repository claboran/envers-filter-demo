package de.laboranowitsch.poc.enversfilterdemo.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.envers.Audited
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.util.UUID

/**
 * Data Container for descriptions.
 */
@Entity
@Table(name = "product_descriptions")
@Audited
data class DescriptionContainerEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var descriptionJson: String
) : Serializable
