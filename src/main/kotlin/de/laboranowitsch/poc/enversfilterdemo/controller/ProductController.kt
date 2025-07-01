package de.laboranowitsch.poc.enversfilterdemo.controller

import de.laboranowitsch.poc.enversfilterdemo.dto.DescriptionDto
import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRequestDto
import de.laboranowitsch.poc.enversfilterdemo.dto.ProductRevisionDto
import de.laboranowitsch.poc.enversfilterdemo.dto.TechnicalDetailsDto
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
    fun createProduct(@RequestBody request: ProductRequestDto): ResponseEntity<ParentEntity> =
        ResponseEntity.ok(productService.createProduct(request))

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: UUID, @RequestBody request: ProductRequestDto): ResponseEntity<ParentEntity> =
        ResponseEntity.ok(productService.updateProduct(id, request))

    @GetMapping("/{id}/history")
    fun getProductHistory(@PathVariable id: UUID): ResponseEntity<List<ProductRevisionDto>> =
        ResponseEntity.ok(historyService.getProductHistory(id))

    @GetMapping("/{id}/technical-details")
    fun getTechnicalDetailsByParentId(@PathVariable id: UUID): ResponseEntity<TechnicalDetailsDto> =
        productService.getTechnicalDetailsByParentId(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @GetMapping("/{id}/descriptions")
    fun getDescriptionsByParentId(@PathVariable id: UUID): ResponseEntity<DescriptionDto> =
        productService.getDescriptionsByParentId(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
