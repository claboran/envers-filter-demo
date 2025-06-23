package de.laboranowitsch.poc.enversfilterdemo.controller

import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRequestDto
import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRevisionDto
import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import de.laboranowitsch.poc.enversfilterdemo.service.ProductHistoryService
import de.laboranowitsch.poc.enversfilterdemo.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
    private val historyService: ProductHistoryService,
) {

    @PostMapping
    fun createProduct(@RequestBody request: ProductRequestDto): ResponseEntity<ParentEntity> {
        val createdProduct = productService.createProduct(request)
        return ResponseEntity.ok(createdProduct)
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: UUID, @RequestBody request: ProductRequestDto): ResponseEntity<ParentEntity> {
        val updatedProduct = productService.updateProduct(id, request)
        return ResponseEntity.ok(updatedProduct)
    }

    @GetMapping("/{id}/history")
    fun getProductHistory(@PathVariable id: UUID): ResponseEntity<List<ProductRevisionDto>> {
        val history = historyService.getProductHistory(id)
        return ResponseEntity.ok(history)
    }
}
