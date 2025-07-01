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
) : Serializable
