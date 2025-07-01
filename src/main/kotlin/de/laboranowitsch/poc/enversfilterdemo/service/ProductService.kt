package de.laboranowitsch.poc.enversfilterdemo.service

import de.laboranowitsch.poc.enversfilterdemo.dto.DescriptionDto
import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRequestDto
import de.laboranowitsch.poc.enversfilterdemo.dto.TechnicalDetailsDto
import de.laboranowitsch.poc.enversfilterdemo.entity.DescriptionContainerEntity
import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import de.laboranowitsch.poc.enversfilterdemo.entity.TechnicalDetailsContainerEntity
import de.laboranowitsch.poc.enversfilterdemo.repo.DescriptionContainerRepository
import de.laboranowitsch.poc.enversfilterdemo.repo.ParentRepository
import de.laboranowitsch.poc.enversfilterdemo.repo.TechnicalDetailsContainerRepository
import de.laboranowitsch.poc.enversfilterdemo.util.LoggingAware
import de.laboranowitsch.poc.enversfilterdemo.util.logger
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductService(
    private val parentRepository: ParentRepository,
    private val technicalDetailsContainerRepository: TechnicalDetailsContainerRepository,
    private val descriptionContainerRepository: DescriptionContainerRepository,
) : LoggingAware {


    @Transactional
    fun createProduct(request: ProductRequestDto): ParentEntity = with(request) {
        ParentEntity(
            name = name,
            status = status,
            technicalDetailsContainer = technicalDetailsJson?.let { TechnicalDetailsContainerEntity(technicalDetailsJson = it) },
            descriptionContainer = descriptionJson?.let { DescriptionContainerEntity(descriptionJson = it) }
        ).let { parentRepository.save(it) }
            .also { logger().info("Creating new product with name: {}", it.name) }
    }

    @Transactional
    fun updateProduct(id: UUID, request: ProductRequestDto): ParentEntity =
        request.updateOrElseThrow(id).let { parentRepository.save(it) }
            .also { logger().info("Updating product with ID: {}", it.id) }

    @Transactional
    fun deleteProduct(id: UUID) =
        parentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product with ID $id not found") }
            .let { parentRepository.delete(it) }
            .also { logger().info("Deleted product with ID: {}", id) }


    private fun ProductRequestDto.updateOrElseThrow(id: UUID): ParentEntity =
        parentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product with ID $id not found") }
            .apply {
                name = this@updateOrElseThrow.name
                status = this@updateOrElseThrow.status

                // Handle technical details
                technicalDetailsContainer = if (this@updateOrElseThrow.technicalDetailsJson != null) {
                    // If we have technical details in the request, update or create the container
                    if (this.technicalDetailsContainer != null) {
                        // Update existing container
                        TechnicalDetailsContainerEntity(
                            id = this.technicalDetailsContainer!!.id,
                            technicalDetailsJson = this@updateOrElseThrow.technicalDetailsJson
                        )
                    } else {
                        // Create new container
                        TechnicalDetailsContainerEntity(
                            technicalDetailsJson = this@updateOrElseThrow.technicalDetailsJson
                        )
                    }
                } else {
                    // If no technical details in request, set to null
                    null
                }

                // Handle description
                descriptionContainer = if (this@updateOrElseThrow.descriptionJson != null) {
                    // If we have description in the request, update or create the container
                    if (this.descriptionContainer != null) {
                        // Update existing container
                        DescriptionContainerEntity(
                            id = this.descriptionContainer!!.id,
                            descriptionJson = this@updateOrElseThrow.descriptionJson
                        )
                    } else {
                        // Create new container
                        DescriptionContainerEntity(
                            descriptionJson = this@updateOrElseThrow.descriptionJson
                        )
                    }
                } else {
                    // If no description in request, set to null
                    null
                }
            }

    /**
     * Get technical details by parent ID.
     *
     * @param parentId the ID of the parent entity
     * @return the technical details if found, null otherwise
     */
    fun getTechnicalDetailsByParentId(parentId: UUID): TechnicalDetailsDto? =
        technicalDetailsContainerRepository.findByParentId(parentId)?.technicalDetailsJson
        .also { logger().info("Retrieved technical details for product with ID: {}", parentId) }

    /**
     * Get descriptions by parent ID.
     *
     * @param parentId the ID of the parent entity
     * @return the descriptions if found, null otherwise
     */
    fun getDescriptionsByParentId(parentId: UUID): DescriptionDto? =
        descriptionContainerRepository.findByParentId(parentId)?.descriptionJson
        .also { logger().info("Retrieved descriptions for product with ID: {}", parentId) }
}
