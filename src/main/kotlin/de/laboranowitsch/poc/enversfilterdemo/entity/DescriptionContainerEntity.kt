package de.laboranowitsch.poc.enversfilterdemo.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.laboranowitsch.poc.enversfilterdemo.dto.DescriptionDto
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
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
    var descriptionJson: DescriptionDto,

    @OneToOne(mappedBy = "descriptionContainer", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("descriptionContainer")
    var parent: ParentEntity? = null
) : Serializable {
    override fun toString(): String {
        return "DescriptionContainerEntity(id=$id, descriptionJson=$descriptionJson)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DescriptionContainerEntity

        if (id != other.id) return false
        if (descriptionJson != other.descriptionJson) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + descriptionJson.hashCode()
        return result
    }
}
