package de.laboranowitsch.poc.enversfilterdemo.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.laboranowitsch.poc.enversfilterdemo.dto.TechnicalDetailsDto
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

@Entity
@Table(name = "product_tech_details")
@Audited
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class TechnicalDetailsContainerEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    var technicalDetailsJson: TechnicalDetailsDto,

    @OneToOne(mappedBy = "technicalDetailsContainer", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("technicalDetailsContainer")
    var parent: ParentEntity? = null
) : Serializable {
    override fun toString(): String {
        return "TechnicalDetailsContainerEntity(id=$id, technicalDetailsJson=$technicalDetailsJson)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TechnicalDetailsContainerEntity

        if (id != other.id) return false
        if (technicalDetailsJson != other.technicalDetailsJson) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + technicalDetailsJson.hashCode()
        return result
    }
}
