package com.ra.bakerysystem.service;

import com.ra.bakerysystem.model.DTO.ProductDTO;
import java.util.List;

public interface ProductService {

    List<ProductDTO> getProducts(Long categoryId, String search, Boolean isActive);
    ProductDTO getProductById(Long id);
}
