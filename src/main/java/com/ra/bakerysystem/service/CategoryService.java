package com.ra.bakerysystem.service;

import com.ra.bakerysystem.model.DTO.CategoryDTO;
import com.ra.bakerysystem.model.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
}
