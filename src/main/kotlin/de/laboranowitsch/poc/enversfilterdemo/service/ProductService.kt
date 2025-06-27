package de.laboranowitsch.poc.enversfilterdemo.service

import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRequestDto
import de.laboranowitsch.poc.enversfilterdemo.entity.DescriptionContainerEntity
import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import de.laboranowitsch.poc.enversfilterdemo.entity.TechnicalDetailsContainerEntity
import de.laboranowitsch.poc.enversfilterdemo.repo.ParentRepository
import de.laboranowitsch.poc.enversfilterdemo.util.LoggingAware
import de.laboranowitsch.poc.enversfilterdemo.util.logger
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductService(
    private val parentRepository: ParentRepository,
) : LoggingAware {


    @Transactional
    fun createProduct(request: ProductRequestDto): ParentEntity {
        logger().info("Creating new product with name: {}", request.name)

        val techDetailsContainer = TechnicalDetailsContainerEntity(
            technicalDetailsJson = request.technicalDetailsJson
        )
        val descriptionContainer = DescriptionContainerEntity(
            descriptionJson = request.descriptionJson
        )

        val parent = ParentEntity(
            name = request.name,
            status = request.status,
            technicalDetailsContainer = techDetailsContainer,
            descriptionContainer = descriptionContainer
        )
        return parentRepository.save(parent)
    }

    @Transactional
    fun updateProduct(id: UUID, request: ProductRequestDto): ParentEntity {
        logger().info("Updating product with ID: {}", id)
        val parent = parentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product with ID $id not found") }

        // Update promoted fields on the parent
        parent.name = request.name
        parent.status = request.status

        // Update JSONB data on the respective containers
        parent.technicalDetailsContainer.technicalDetailsJson = request.technicalDetailsJson
        parent.descriptionContainer.descriptionJson = request.descriptionJson

        // Saving the parent will cascade changes to its containers and trigger Envers for all three entities
        return parentRepository.save(parent)
    }

    @Transactional
    fun deleteProduct(id: UUID) {
        logger().info("Deleting product with ID: {}", id)
        val product = parentRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product with ID $id not found") }

        parentRepository.delete(product)
    }
}
