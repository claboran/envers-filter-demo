package de.laboranowitsch.poc.enversfilterdemo.repo

import de.laboranowitsch.poc.enversfilterdemo.entity.TechnicalDetailsContainerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

/**
 * Repository for TechnicalDetailsContainerEntity.
 */
interface TechnicalDetailsContainerRepository : JpaRepository<TechnicalDetailsContainerEntity, UUID> {
    
    /**
     * Find TechnicalDetailsContainerEntity by parent ID.
     *
     * @param parentId the ID of the parent entity
     * @return the TechnicalDetailsContainerEntity if found
     */
    @Query("SELECT t FROM TechnicalDetailsContainerEntity t WHERE t.parent.id = :parentId")
    fun findByParentId(@Param("parentId") parentId: UUID): Optional<TechnicalDetailsContainerEntity>
}