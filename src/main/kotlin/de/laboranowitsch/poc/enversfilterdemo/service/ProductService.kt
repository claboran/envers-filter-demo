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
    fun createProduct(request: ProductRequestDto): ParentEntity = with(request) {
        ParentEntity(
            name = name,
            status = status,
            technicalDetailsContainer = TechnicalDetailsContainerEntity(technicalDetailsJson = technicalDetailsJson),
            descriptionContainer = DescriptionContainerEntity(descriptionJson = descriptionJson)
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
                technicalDetailsContainer = TechnicalDetailsContainerEntity(
                    id = this.technicalDetailsContainer.id,
                    technicalDetailsJson = this@updateOrElseThrow.technicalDetailsJson,
                )
                descriptionContainer = DescriptionContainerEntity(
                    id = this.descriptionContainer.id,
                    descriptionJson = this@updateOrElseThrow.descriptionJson,
                )
            }
}
