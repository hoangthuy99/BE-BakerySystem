package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.model.DTO.ProductDTO;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getProducts(Long categoryId, String search, Boolean isActive) {
        return productRepository
                .findProducts(categoryId, search, isActive)
                .stream()
                .map(ProductDTO::new)
                .toList();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::new)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}