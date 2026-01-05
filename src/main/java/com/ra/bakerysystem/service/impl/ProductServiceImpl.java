package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.model.DTO.ProductDTO;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.ProductService;
import com.ra.bakerysystem.utils.ConvertImageUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ConvertImageUrl convertImageUrl;
    @Override
    public List<ProductDTO> getProducts(Long categoryId, String search, Boolean isActive) {
        return productRepository
                .findProducts(categoryId, search, isActive)
                .stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO(product);
                    dto.setImageUrl(convertImageUrl.buildImageUrl(product.getImageUrl()));
                    return dto;
                })
                .toList();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDTO dto = new ProductDTO(product);
        dto.setImageUrl(convertImageUrl.buildImageUrl(product.getImageUrl()));
        return dto;
    }
}