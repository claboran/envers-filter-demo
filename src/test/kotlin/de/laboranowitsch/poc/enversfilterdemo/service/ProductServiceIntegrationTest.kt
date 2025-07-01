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
        assertNotNull(createdProduct.technicalDetailsContainer)
        assertEquals("500W", createdProduct.technicalDetailsContainer?.technicalDetailsJson?.power)
        assertNotNull(createdProduct.descriptionContainer)
        assertEquals("A test product", createdProduct.descriptionContainer?.descriptionJson?.descriptions?.get("en"))

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
        assertNotNull(updatedProduct.technicalDetailsContainer)
        assertEquals("750W", updatedProduct.technicalDetailsContainer?.technicalDetailsJson?.power)
        assertEquals("20Nm", updatedProduct.technicalDetailsContainer?.technicalDetailsJson?.torque)
        assertNotNull(updatedProduct.descriptionContainer)
        assertEquals("An updated product", updatedProduct.descriptionContainer?.descriptionJson?.descriptions?.get("en"))

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

    @Test
    @Transactional
    fun `should create product without technical details`() {
        // Given
        val productRequest = ProductRequestDto(
            name = "Product Without Tech Details",
            status = "AVAILABLE",
            technicalDetailsJson = null,
            descriptionJson = DescriptionDto(
                descriptions = mapOf("en" to "A product without technical details")
            )
        )

        // When
        val createdProduct = productService.createProduct(productRequest)

        // Then
        assertNotNull(createdProduct.id)
        assertEquals("Product Without Tech Details", createdProduct.name)
        assertEquals("AVAILABLE", createdProduct.status)
        assertNull(createdProduct.technicalDetailsContainer)
        assertNotNull(createdProduct.descriptionContainer)
        assertEquals("A product without technical details", createdProduct.descriptionContainer?.descriptionJson?.descriptions?.get("en"))

        // Verify it's in the database
        entityManager.flush()
        entityManager.clear()

        val foundProduct = entityManager.find(ParentEntity::class.java, createdProduct.id)
        assertNotNull(foundProduct)
        assertEquals("Product Without Tech Details", foundProduct.name)
        assertNull(foundProduct.technicalDetailsContainer)
        assertNotNull(foundProduct.descriptionContainer)
    }

    @Test
    @Transactional
    fun `should create product without description`() {
        // Given
        val productRequest = ProductRequestDto(
            name = "Product Without Description",
            status = "AVAILABLE",
            technicalDetailsJson = TechnicalDetailsDto(
                power = "500W"
            ),
            descriptionJson = null
        )

        // When
        val createdProduct = productService.createProduct(productRequest)

        // Then
        assertNotNull(createdProduct.id)
        assertEquals("Product Without Description", createdProduct.name)
        assertEquals("AVAILABLE", createdProduct.status)
        assertNotNull(createdProduct.technicalDetailsContainer)
        assertEquals("500W", createdProduct.technicalDetailsContainer?.technicalDetailsJson?.power)
        assertNull(createdProduct.descriptionContainer)

        // Verify it's in the database
        entityManager.flush()
        entityManager.clear()

        val foundProduct = entityManager.find(ParentEntity::class.java, createdProduct.id)
        assertNotNull(foundProduct)
        assertEquals("Product Without Description", foundProduct.name)
        assertNotNull(foundProduct.technicalDetailsContainer)
        assertNull(foundProduct.descriptionContainer)
    }

    @Test
    @Transactional
    fun `should create product without any details`() {
        // Given
        val productRequest = ProductRequestDto(
            name = "Product Without Any Details",
            status = "AVAILABLE",
            technicalDetailsJson = null,
            descriptionJson = null
        )

        // When
        val createdProduct = productService.createProduct(productRequest)

        // Then
        assertNotNull(createdProduct.id)
        assertEquals("Product Without Any Details", createdProduct.name)
        assertEquals("AVAILABLE", createdProduct.status)
        assertNull(createdProduct.technicalDetailsContainer)
        assertNull(createdProduct.descriptionContainer)

        // Verify it's in the database
        entityManager.flush()
        entityManager.clear()

        val foundProduct = entityManager.find(ParentEntity::class.java, createdProduct.id)
        assertNotNull(foundProduct)
        assertEquals("Product Without Any Details", foundProduct.name)
        assertNull(foundProduct.technicalDetailsContainer)
        assertNull(foundProduct.descriptionContainer)
    }

    @Test
    @Transactional
    fun `should update product to add and remove details`() {
        // Given - Create product without any details
        val initialRequest = ProductRequestDto(
            name = "Initial Product",
            status = "AVAILABLE",
            technicalDetailsJson = null,
            descriptionJson = null
        )
        val createdProduct = productService.createProduct(initialRequest)
        assertNull(createdProduct.technicalDetailsContainer)
        assertNull(createdProduct.descriptionContainer)

        // When - Update to add technical details
        val addTechRequest = ProductRequestDto(
            name = "Updated Product",
            status = "IN_STOCK",
            technicalDetailsJson = TechnicalDetailsDto(
                power = "750W",
                torque = "20Nm"
            ),
            descriptionJson = null
        )
        val productWithTech = productService.updateProduct(createdProduct.id!!, addTechRequest)

        // Then
        assertNotNull(productWithTech.technicalDetailsContainer)
        assertEquals("750W", productWithTech.technicalDetailsContainer?.technicalDetailsJson?.power)
        assertNull(productWithTech.descriptionContainer)

        // When - Update to add description and remove technical details
        val switchDetailsRequest = ProductRequestDto(
            name = "Switched Details",
            status = "OUT_OF_STOCK",
            technicalDetailsJson = null,
            descriptionJson = DescriptionDto(
                descriptions = mapOf("en" to "Now with description only")
            )
        )
        val productWithSwitchedDetails = productService.updateProduct(createdProduct.id!!, switchDetailsRequest)

        // Then
        assertNull(productWithSwitchedDetails.technicalDetailsContainer)
        assertNotNull(productWithSwitchedDetails.descriptionContainer)
        assertEquals("Now with description only", productWithSwitchedDetails.descriptionContainer?.descriptionJson?.descriptions?.get("en"))
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
