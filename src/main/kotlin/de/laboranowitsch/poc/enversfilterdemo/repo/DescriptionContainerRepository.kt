package de.laboranowitsch.poc.enversfilterdemo.repo

import de.laboranowitsch.poc.enversfilterdemo.entity.DescriptionContainerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

/**
 * Repository for DescriptionContainerEntity.
 */
interface DescriptionContainerRepository : JpaRepository<DescriptionContainerEntity, UUID> {
    
    /**
     * Find DescriptionContainerEntity by parent ID.
     *
     * @param parentId the ID of the parent entity
     * @return the DescriptionContainerEntity if found
     */
    @Query("SELECT d FROM DescriptionContainerEntity d WHERE d.parent.id = :parentId")
    fun findByParentId(@Param("parentId") parentId: UUID): Optional<DescriptionContainerEntity>
}