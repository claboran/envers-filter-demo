package de.laboranowitsch.poc.enversfilterdemo.service

import de.laboranowitsch.poc.enversfilterdemo.dto.DescriptionDto
import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRequestDto
import de.laboranowitsch.poc.enversfilterdemo.dto.TechnicalDetailsDto
import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import de.laboranowitsch.poc.enversfilterdemo.util.PostgresIntegrationTest
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@PostgresIntegrationTest
class ProductServiceIntegrationTest {

    @Autowired
    private lateinit var productService: ProductService

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Test
    @Transactional
    fun `should create product with all details`() {
        // Given
        val productRequest = createTestProductRequest("Test Product", "AVAILABLE")

        // When
        val createdProduct = productService.createProduct(productRequest)

        // Then
        assertNotNull(createdProduct.id)
        assertEquals("Test Product", createdProduct.name)
        assertEquals("AVAILABLE", createdProduct.status)
        assertEquals("500W", createdProduct.technicalDetailsContainer.technicalDetailsJson.power)
        assertEquals("A test product", createdProduct.descriptionContainer.descriptionJson.descriptions["en"])

        // Verify it's in the database
        entityManager.flush()
        entityManager.clear()

        val foundProduct = entityManager.find(ParentEntity::class.java, createdProduct.id)
        assertNotNull(foundProduct)
        assertEquals("Test Product", foundProduct.name)
    }

    @Test
    @Transactional
    fun `should update product with new details`() {
        // Given
        val productRequest = createTestProductRequest("Original Product", "IN_STOCK")
        val createdProduct = productService.createProduct(productRequest)

        // When
        val updateRequest = ProductRequestDto(
            name = "Updated Product",
            status = "OUT_OF_STOCK",
            technicalDetailsJson = TechnicalDetailsDto(
                power = "750W",
                torque = "20Nm"
            ),
            descriptionJson = DescriptionDto(
                descriptions = mapOf("en" to "An updated product")
            )
        )

        val updatedProduct = productService.updateProduct(createdProduct.id!!, updateRequest)

        // Then
        assertEquals(createdProduct.id, updatedProduct.id)
        assertEquals("Updated Product", updatedProduct.name)
        assertEquals("OUT_OF_STOCK", updatedProduct.status)
        assertEquals("750W", updatedProduct.technicalDetailsContainer.technicalDetailsJson.power)
        assertEquals("20Nm", updatedProduct.technicalDetailsContainer.technicalDetailsJson.torque)
        assertEquals("An updated product", updatedProduct.descriptionContainer.descriptionJson.descriptions["en"])

        // Verify it's updated in the database
        entityManager.flush()
        entityManager.clear()

        val foundProduct = entityManager.find(ParentEntity::class.java, updatedProduct.id)
        assertEquals("Updated Product", foundProduct.name)
        assertEquals("OUT_OF_STOCK", foundProduct.status)
    }

    @Test
    @Transactional
    fun `should delete product`() {
        // Given
        val productRequest = createTestProductRequest("Product to Delete", "AVAILABLE")
        val createdProduct = productService.createProduct(productRequest)
        val productId = createdProduct.id!!

        // Verify it exists
        assertNotNull(entityManager.find(ParentEntity::class.java, productId))

        // When
        productService.deleteProduct(productId)

        // Then
        entityManager.flush()
        entityManager.clear()

        // Product should no longer exist
        assertNull(entityManager.find(ParentEntity::class.java, productId))
    }

    @Test
    fun `should throw exception when deleting non-existent product`() {
        // Given
        val nonExistentId = UUID.randomUUID()

        // When/Then
        assertThrows<EntityNotFoundException> {
            productService.deleteProduct(nonExistentId)
        }
    }

    private fun createTestProductRequest(name: String, status: String): ProductRequestDto {
        return ProductRequestDto(
            name = name,
            status = status,
            technicalDetailsJson = TechnicalDetailsDto(
                power = "500W"
            ),
            descriptionJson = DescriptionDto(
                descriptions = mapOf("en" to "A test product")
            )
        )
    }
}
