package de.laboranowitsch.poc.enversfilterdemo

import de.laboranowitsch.poc.enversfilterdemo.dto.DescriptionDto
import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRequestDto
import de.laboranowitsch.poc.enversfilterdemo.dto.TechnicalDetailsDto
import de.laboranowitsch.poc.enversfilterdemo.util.PostgresIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PostgresIntegrationTest
class ProductControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should create product, update it, and retrieve history`() {
        // 1. Create a product
        val createRequest = ProductRequestDto(
            name = "Test Drill",
            status = "IN_STOCK",
            technicalDetailsJson = TechnicalDetailsDto(
                power = "500W"
            ),
            descriptionJson = DescriptionDto(
                descriptions = mapOf("en" to "A reliable test drill.")
            )
        )
        val createResponse = restTemplate.postForEntity<Map<String, Any>>("/api/products", createRequest)
        assertEquals(HttpStatus.OK, createResponse.statusCode)
        assertNotNull(createResponse.body)
        val productId = UUID.fromString(createResponse.body!!["id"] as String)
        assertEquals("Test Drill", createResponse.body!!["name"])

        // 2. Update the product
        val updateRequest = ProductRequestDto(
            name = "Test Drill PRO",
            status = "OUT_OF_STOCK",
            technicalDetailsJson = TechnicalDetailsDto(
                power = "750W",
                torque = "20Nm"
            ),
            descriptionJson = DescriptionDto(
                descriptions = mapOf("en" to "A professional-grade test drill.")
            )
        )
        restTemplate.put("/api/products/{id}", updateRequest, productId)


        // 3. Get the history
        val historyResponse = restTemplate.getForEntity<List<Map<String, Any>>>("/api/products/{id}/history", productId)
        assertEquals(HttpStatus.OK, historyResponse.statusCode)
        val history = historyResponse.body
        assertNotNull(history)
        assertEquals(2, history!!.size, "Should have two revisions (create and update)")

        // 4. Verify Revision 1 (Creation)
        val revision1Product = history[0]["product"] as Map<*, *>
        assertEquals("Test Drill", revision1Product["name"])
        assertEquals("IN_STOCK", revision1Product["status"])

        // Check if technicalDetailsContainer exists
        assertNotNull(revision1Product["technicalDetailsContainer"])
        val techDetails1 = revision1Product["technicalDetailsContainer"] as Map<*, *>
        val techDetailsJson1 = techDetails1["technicalDetailsJson"] as Map<*, *>
        assertEquals("500W", techDetailsJson1["power"])

        // 5. Verify Revision 2 (Update)
        val revision2Product = history[1]["product"] as Map<*, *>
        assertEquals("Test Drill PRO", revision2Product["name"])
        assertEquals("OUT_OF_STOCK", revision2Product["status"])

        // Check if technicalDetailsContainer exists
        assertNotNull(revision2Product["technicalDetailsContainer"])
        val techDetails2 = revision2Product["technicalDetailsContainer"] as Map<*, *>
        val techDetailsJson2 = techDetails2["technicalDetailsJson"] as Map<*, *>
        assertEquals("750W", techDetailsJson2["power"])
        assertEquals("20Nm", techDetailsJson2["torque"])
    }
}
