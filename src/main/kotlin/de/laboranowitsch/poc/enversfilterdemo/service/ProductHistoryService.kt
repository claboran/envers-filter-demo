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

        // For each revision, find the state of the entity at that point in time
        // Envers will automatically fetch the related container entities as they existed at that revision
        return revisions.map { revNumber ->
            val entityAtRevision = auditReader.find(ParentEntity::class.java, id, revNumber)
            val revisionEntity = auditReader.findRevision(DefaultRevisionEntity::class.java, revNumber)
            ProductRevisionDto(
                revisionNumber = revNumber,
                revisionTimestamp = revisionEntity.revisionDate.toInstant(),
                product = entityAtRevision,
            )
        }
    }
}
