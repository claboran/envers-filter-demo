package de.laboranowitsch.poc.enversfilterdemo.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.laboranowitsch.poc.enversfilterdemo.dto.DescriptionDto
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import org.hibernate.envers.Audited
import java.io.Serializable
import java.util.UUID

/**
 * Data Container for descriptions.
 */
@Entity
@Table(name = "product_descriptions")
@Audited
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class DescriptionContainerEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    var descriptionJson: DescriptionDto
) : Serializable
