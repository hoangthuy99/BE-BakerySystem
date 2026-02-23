package com.ra.bakerysystem.controller;

import com.ra.bakerysystem.model.DTO.PosDTO;
import com.ra.bakerysystem.model.DTO.ProductDTO;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@RestController
@RequestMapping("/app/products")
@RequiredArgsConstructor
@Tag(name = "Product API")
public class ProductController {
    @Value("${path-upload}")
    private String uploadDir;

    private final ProductService productService;

    @GetMapping
    public List<ProductDTO> getProducts(
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(value = "is_active", required = false) Boolean isActive
    ) {
        return productService.getProducts(categoryId, search, isActive);
    }

    @GetMapping("/{id}")
    public ProductDTO getProductDetail(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping(
            value = "/add-product",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createProduct(
            @Valid @ModelAttribute ProductDTO productRequest,
            BindingResult bindingResult,
            @RequestParam(value = "img", required = false) MultipartFile image
    ) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(bindingResult.getAllErrors());
        }

        String imagePath = null;

        if (image != null && !image.isEmpty()) {
            try {
                BufferedImage originalImage = ImageIO.read(image.getInputStream());
                if (originalImage == null) {
                    return ResponseEntity.badRequest().body("Tệp tải lên không hợp lệ!");
                }

                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);

                Files.copy(
                        image.getInputStream(),
                        uploadPath.resolve(image.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING
                );

                imagePath = image.getOriginalFilename();

            } catch (IOException e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi lưu ảnh");
            }
        }

        Product product = productService.createProduct(productRequest, imagePath, null);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductDTO productRequest,
            BindingResult bindingResult,
            @RequestParam(value = "img", required = false) MultipartFile image
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(bindingResult.getAllErrors());
        }

        String imagePath = null;

        if (image != null && !image.isEmpty()) {
            try {
                BufferedImage originalImage = ImageIO.read(image.getInputStream());
                if (originalImage == null) {
                    return ResponseEntity.badRequest().body("Tệp tải lên không hợp lệ!");
                }

                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);

                Files.copy(
                        image.getInputStream(),
                        uploadPath.resolve(image.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING
                );

                imagePath = image.getOriginalFilename();

            } catch (IOException e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi lưu ảnh");
            }
        }

        Product product = productService.updateProduct(id, productRequest, imagePath);

        return ResponseEntity.ok(new ProductDTO(product));
    }
    // lớp lấy dữ liệu cho POS có kèm product và inventory
    @GetMapping("/product-pos")
    public ResponseEntity<List<PosDTO>> getProductsForPos(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId
    ) {
        return ResponseEntity.ok(
                productService.getProductsForPos(keyword, categoryId)
        );
    }

}
