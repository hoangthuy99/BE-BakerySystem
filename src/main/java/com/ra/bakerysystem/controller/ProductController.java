package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.model.DTO.ProductDTO;
import com.ra.bakerysystem.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get product list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public List<ProductDTO> getProducts(
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(value = "is_active", required = false) Boolean isActive
    ) {
        return productService.getProducts(categoryId, search, isActive);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product detail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ProductDTO getProductDetail(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
