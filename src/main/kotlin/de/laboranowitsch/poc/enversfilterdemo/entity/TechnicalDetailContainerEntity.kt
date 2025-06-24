package de.laboranowitsch.poc.enversfilterdemo.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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

@Entity
@Table(name = "product_tech_details")
@Audited
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class TechnicalDetailsContainerEntity(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var technicalDetailsJson: String
) : Serializable
