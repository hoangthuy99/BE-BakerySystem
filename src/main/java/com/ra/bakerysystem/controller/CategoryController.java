package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.model.DTO.CategoryDTO;
import com.ra.bakerysystem.model.entity.Category;
import com.ra.bakerysystem.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/app/category")
@RequiredArgsConstructor
@Tag(name = "Category API")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/all")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
