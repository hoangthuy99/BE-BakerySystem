package com.ra.bakerysystem.service;

import com.ra.bakerysystem.model.DTO.PosDTO;
import com.ra.bakerysystem.model.DTO.ProductDTO;
import com.ra.bakerysystem.model.entity.Product;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getProducts(Long categoryId, String search, Boolean isActive);
    ProductDTO getProductById(Long id);
    Product createProduct(ProductDTO productDTO, String imagePath, Long id);
    Product updateProduct(Long id, ProductDTO productDTO, String imagePath);
    List<PosDTO> getProductsForPos(String keyword, Long categoryId);
}
