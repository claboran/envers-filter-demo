package de.laboranowitsch.poc.enversfilterdemo.repo

import de.laboranowitsch.poc.enversfilterdemo.dto.DescriptionDto
import de.laboranowitsch.poc.enversfilterdemo.entity.DescriptionContainerEntity
import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import de.laboranowitsch.poc.enversfilterdemo.util.PostgresIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@PostgresIntegrationTest
@Transactional
class DescriptionContainerRepositoryTest {

    @Autowired
    private lateinit var parentRepository: ParentRepository

    @Autowired
    private lateinit var descriptionContainerRepository: DescriptionContainerRepository

    @Test
    fun `should find description by parent id`() {
        // Given
        val description = DescriptionContainerEntity(
            descriptionJson = DescriptionDto(
                descriptions = mapOf(
                    "en" to "English description",
                    "de" to "Deutsche Beschreibung"
                )
            )
        )
        
        val parent = ParentEntity(
            name = "Test Product",
            status = "ACTIVE"
        )
        
        // Use the helper method to set up the bi-directional relationship
        parent.addDescriptionContainer(description)
        
        // Save the parent entity which will cascade to the description
        val savedParent = parentRepository.save(parent)
        
        // When
        val foundDescription = descriptionContainerRepository.findByParentId(savedParent.id!!)
        
        // Then
        assertTrue(foundDescription.isPresent)
        assertEquals(description.id, foundDescription.get().id)
        assertEquals(description.descriptionJson.descriptions, foundDescription.get().descriptionJson.descriptions)
        assertEquals(savedParent.id, foundDescription.get().parent?.id)
    }
    
    @Test
    fun `should return empty when parent id does not exist`() {
        // When
        val foundDescription = descriptionContainerRepository.findByParentId(java.util.UUID.randomUUID())
        
        // Then
        assertTrue(foundDescription.isEmpty)
    }
    
    @Test
    fun `should update both sides of relationship when using helper methods`() {
        // Given
        val description = DescriptionContainerEntity(
            descriptionJson = DescriptionDto(
                descriptions = mapOf(
                    "en" to "English description",
                    "de" to "Deutsche Beschreibung"
                )
            )
        )
        
        val parent = ParentEntity(
            name = "Test Product",
            status = "ACTIVE"
        )
        
        // When
        parent.addDescriptionContainer(description)
        val savedParent = parentRepository.save(parent)
        
        // Then
        val foundParent = parentRepository.findById(savedParent.id!!).get()
        val foundDescription = descriptionContainerRepository.findById(foundParent.descriptionContainer!!.id!!).get()
        
        assertEquals(foundParent.id, foundDescription.parent?.id)
        assertEquals(foundDescription.id, foundParent.descriptionContainer?.id)
        
        // When removing
        foundParent.removeDescriptionContainer()
        parentRepository.save(foundParent)
        
        // Then
        val updatedParent = parentRepository.findById(savedParent.id!!).get()
        assertEquals(null, updatedParent.descriptionContainer)
        
        // The description entity should still exist but with no parent reference
        val orphanedDescription = descriptionContainerRepository.findById(foundDescription.id!!).get()
        assertEquals(null, orphanedDescription.parent)
    }
}