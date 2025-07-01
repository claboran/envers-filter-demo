package de.laboranowitsch.poc.enversfilterdemo.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.envers.Audited
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "products")
@Audited
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class ParentEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, updatable = false)
    val creationTimestamp: Instant = Instant.now(),

    @Column(nullable = false, name = "product_name")
    var name: String,

    @Column(nullable = false)
    var status: String,

    // --- One-to-One Relationship to Technical Details ---
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "tech_details_id", referencedColumnName = "id")
    var technicalDetailsContainer: TechnicalDetailsContainerEntity? = null,

    // --- One-to-One Relationship to Descriptions ---
    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "description_id", referencedColumnName = "id")
    var descriptionContainer: DescriptionContainerEntity? = null,
) : Serializable {

    /**
     * Add technical details container and set the parent reference.
     *
     * @param technicalDetails the technical details container to add
     */
    fun addTechnicalDetailsContainer(technicalDetails: TechnicalDetailsContainerEntity) {
        technicalDetailsContainer = technicalDetails
        technicalDetails.parent = this
    }

    /**
     * Remove technical details container and clear the parent reference.
     */
    fun removeTechnicalDetailsContainer() {
        technicalDetailsContainer?.parent = null
        technicalDetailsContainer = null
    }

    /**
     * Add description container and set the parent reference.
     *
     * @param description the description container to add
     */
    fun addDescriptionContainer(description: DescriptionContainerEntity) {
        descriptionContainer = description
        description.parent = this
    }

    /**
     * Remove description container and clear the parent reference.
     */
    fun removeDescriptionContainer() {
        descriptionContainer?.parent = null
        descriptionContainer = null
    }

    override fun toString(): String {
        return "ParentEntity(id=$id, creationTimestamp=$creationTimestamp, name='$name', status='$status')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParentEntity

        if (id != other.id) return false
        if (creationTimestamp != other.creationTimestamp) return false
        if (name != other.name) return false
        if (status != other.status) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + creationTimestamp.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}
