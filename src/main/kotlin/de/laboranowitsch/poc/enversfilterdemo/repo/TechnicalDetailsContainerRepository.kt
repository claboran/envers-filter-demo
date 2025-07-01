package de.laboranowitsch.poc.enversfilterdemo.repo

import de.laboranowitsch.poc.enversfilterdemo.entity.TechnicalDetailsContainerEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

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
    fun findByParentId(parentId: UUID): TechnicalDetailsContainerEntity?
}