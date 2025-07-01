package de.laboranowitsch.poc.enversfilterdemo.repo

import de.laboranowitsch.poc.enversfilterdemo.dto.TechnicalDetailsDto
import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import de.laboranowitsch.poc.enversfilterdemo.entity.TechnicalDetailsContainerEntity
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
class TechnicalDetailsContainerRepositoryTest {

    @Autowired
    private lateinit var parentRepository: ParentRepository

    @Autowired
    private lateinit var technicalDetailsContainerRepository: TechnicalDetailsContainerRepository

    @Test
    fun `should find technical details by parent id`() {
        // Given
        val technicalDetails = TechnicalDetailsContainerEntity(
            technicalDetailsJson = TechnicalDetailsDto(
                power = "100 kW",
                torque = "200 Nm",
                additionalProperties = mapOf("color" to "red", "size" to "large")
            )
        )
        
        val parent = ParentEntity(
            name = "Test Product",
            status = "ACTIVE"
        )
        
        // Use the helper method to set up the bi-directional relationship
        parent.addTechnicalDetailsContainer(technicalDetails)
        
        // Save the parent entity which will cascade to the technical details
        val savedParent = parentRepository.save(parent)
        
        // When
        val foundTechnicalDetails = technicalDetailsContainerRepository.findByParentId(savedParent.id!!)
        
        // Then
        assertTrue(foundTechnicalDetails.isPresent)
        assertEquals(technicalDetails.id, foundTechnicalDetails.get().id)
        assertEquals(technicalDetails.technicalDetailsJson.power, foundTechnicalDetails.get().technicalDetailsJson.power)
        assertEquals(technicalDetails.technicalDetailsJson.torque, foundTechnicalDetails.get().technicalDetailsJson.torque)
        assertEquals(technicalDetails.technicalDetailsJson.additionalProperties, foundTechnicalDetails.get().technicalDetailsJson.additionalProperties)
        assertEquals(savedParent.id, foundTechnicalDetails.get().parent?.id)
    }
    
    @Test
    fun `should return empty when parent id does not exist`() {
        // When
        val foundTechnicalDetails = technicalDetailsContainerRepository.findByParentId(java.util.UUID.randomUUID())
        
        // Then
        assertTrue(foundTechnicalDetails.isEmpty)
    }
    
    @Test
    fun `should update both sides of relationship when using helper methods`() {
        // Given
        val technicalDetails = TechnicalDetailsContainerEntity(
            technicalDetailsJson = TechnicalDetailsDto(
                power = "100 kW",
                torque = "200 Nm"
            )
        )
        
        val parent = ParentEntity(
            name = "Test Product",
            status = "ACTIVE"
        )
        
        // When
        parent.addTechnicalDetailsContainer(technicalDetails)
        val savedParent = parentRepository.save(parent)
        
        // Then
        val foundParent = parentRepository.findById(savedParent.id!!).get()
        val foundTechnicalDetails = technicalDetailsContainerRepository.findById(foundParent.technicalDetailsContainer!!.id!!).get()
        
        assertEquals(foundParent.id, foundTechnicalDetails.parent?.id)
        assertEquals(foundTechnicalDetails.id, foundParent.technicalDetailsContainer?.id)
        
        // When removing
        foundParent.removeTechnicalDetailsContainer()
        parentRepository.save(foundParent)
        
        // Then
        val updatedParent = parentRepository.findById(savedParent.id!!).get()
        assertEquals(null, updatedParent.technicalDetailsContainer)
        
        // The technical details entity should still exist but with no parent reference
        val orphanedTechnicalDetails = technicalDetailsContainerRepository.findById(foundTechnicalDetails.id!!).get()
        assertEquals(null, orphanedTechnicalDetails.parent)
    }
}