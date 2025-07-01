package de.laboranowitsch.poc.enversfilterdemo.service

import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRevisionDto
import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import de.laboranowitsch.poc.enversfilterdemo.util.LoggingAware
import de.laboranowitsch.poc.enversfilterdemo.util.logger
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.hibernate.envers.AuditReader
import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.DefaultRevisionEntity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductHistoryService(
    @PersistenceContext private val entityManager: EntityManager,
): LoggingAware {


    @Transactional(readOnly = true)
    fun getProductHistory(id: UUID): List<ProductRevisionDto> {
        logger().info("Fetching history for product ID: {}", id)
        val auditReader: AuditReader = AuditReaderFactory.get(entityManager)

        // Get the revision numbers and timestamps for the ParentEntity
        val revisions = auditReader.getRevisions(ParentEntity::class.java, id)

        if (revisions.isEmpty()) {
            logger().info("No revisions found for product ID: {}", id)
            return emptyList()
        }

        // For each revision, find the state of the entity at that point in time
        // Envers will automatically fetch the related container entities as they existed at that revision
        return revisions.mapNotNull { revNumber ->
            try {
                val entityAtRevision = auditReader.find(ParentEntity::class.java, id, revNumber)
                val revisionEntity = auditReader.findRevision(DefaultRevisionEntity::class.java, revNumber)

                // Skip null entities (might happen for deleted entities in some cases)
                if (entityAtRevision == null) {
                    logger().warn("Entity at revision {} is null for product ID: {}", revNumber, id)
                    return@mapNotNull null
                }

                // Eagerly initialize the technical details and description containers to avoid LazyInitializationException
                entityAtRevision.technicalDetailsContainer?.let {
                    // Access the properties to force initialization
                    it.technicalDetailsJson.toString()
                    // Clear the parent reference to avoid recursion issues
                    it.parent = null
                }

                entityAtRevision.descriptionContainer?.let {
                    // Access the properties to force initialization
                    it.descriptionJson.toString()
                    // Clear the parent reference to avoid recursion issues
                    it.parent = null
                }

                ProductRevisionDto(
                    revisionNumber = revNumber,
                    revisionTimestamp = revisionEntity.revisionDate.toInstant(),
                    product = entityAtRevision,
                )
            } catch (e: Exception) {
                logger().error("Error retrieving revision {} for product ID: {}", revNumber, id, e)
                null
            }
        }
    }
}
