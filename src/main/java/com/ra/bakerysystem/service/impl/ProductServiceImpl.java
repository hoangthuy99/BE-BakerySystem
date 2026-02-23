package com.ra.bakerysystem.service.impl;

import com.ra.bakerysystem.model.DTO.InventoryDTO;
import com.ra.bakerysystem.model.DTO.PosDTO;
import com.ra.bakerysystem.model.DTO.ProductDTO;
import com.ra.bakerysystem.model.entity.Category;
import com.ra.bakerysystem.model.entity.Inventory;
import com.ra.bakerysystem.model.entity.Product;
import com.ra.bakerysystem.repository.CategoryRepository;
import com.ra.bakerysystem.repository.InventoryRepository;
import com.ra.bakerysystem.repository.ProductRepository;
import com.ra.bakerysystem.service.ProductService;
import com.ra.bakerysystem.utils.ConvertImageUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Value("${path-upload}")
    private String basePath;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final ConvertImageUrl convertImageUrl;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
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

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO, String imagePath, Long id) {
        try {

            if (id == null) {
                if (productRepository.existsByName(productDTO.getName())) {
                    throw new IllegalArgumentException("Tên sản phẩm đã tồn tại!");
                }
            } else {
                Optional<Product> existingByName = productRepository.findByName(productDTO.getName());
                if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Tên sản phẩm đã tồn tại!");
                }
            }

            if (productDTO.getCategoryId() == null) {
                throw new IllegalArgumentException("ID danh mục không được để trống");
            }

            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("ID danh mục không hợp lệ"));

            Product product;

            if (id == null) {
                product = new Product();
                product.setCode(Product.generateOrderCode());
            } else {
                product = productRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
            }

            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setQty(productDTO.getQty());
            product.setActive(productDTO.getIsActive());
            product.setType(productDTO.getType());
            product.setAlcoholic(productDTO.getAlcoholic());
            product.setCategory(category);
            product.setImageUrl(imagePath);

            Product savedProduct = productRepository.save(product);
            productRepository.flush();
            if (id == null) {

                Inventory inventory = Inventory.builder()
                        .product(savedProduct)
                        .currentQuantity(productDTO.getQty())
                        .minThreshold(10)
                        .build();

                inventoryRepository.save(inventory);
            }

            return savedProduct;

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Lỗi khi lưu sản phẩm", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống", e);
        }
    }


    @Override
    public Product updateProduct(Long id, ProductDTO productDTO, String imagePath) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

            // kiểm tra trùng tên (trừ chính nó)
            Optional<Product> existingByName = productRepository.findByName(productDTO.getName());
            if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Tên sản phẩm đã tồn tại!");
            }

            if (productDTO.getCategoryId() == null) {
                throw new IllegalArgumentException("ID danh mục không được để trống");
            }

            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("ID danh mục không hợp lệ"));

            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            if (productDTO.getIsActive() != null) {
                product.setActive(productDTO.getIsActive());
            }
            product.setType(productDTO.getType());
            product.setAlcoholic(productDTO.getAlcoholic());
            product.setCategory(category);

            // chỉ update ảnh nếu có upload mới
            if (imagePath != null) {
                product.setImageUrl(imagePath);
            }

            return productRepository.save(product);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lỗi hệ thống, vui lòng thử lại!",
                    e
            );
        }
    }

    @Override
    public List<PosDTO> getProductsForPos(String keyword, Long categoryId) {
        List<Object[]> rows = productRepository.findProductsWithInventory(
                (keyword == null || keyword.isBlank()) ? null : keyword,
                categoryId
        );

        return rows.stream().map(row -> {
            Product product = (Product) row[0];
            Inventory inventory = (Inventory) row[1];

            ProductDTO productDTO = new ProductDTO(product);

            InventoryDTO inventoryDTO = inventory == null ? null :
                    InventoryDTO.builder()
                            .inventoryId(inventory.getInventoryId())
                            .productId(product.getId())
                            .productName(product.getName())
                            .imageUrl(product.getImageUrl())
                            .currentQuantity(inventory.getCurrentQuantity())
                            .minThreshold(inventory.getMinThreshold())
                            .lastUpdated(inventory.getLastUpdated())
                            .build();

            return PosDTO.builder()
                    .product(productDTO)
                    .inventory(inventoryDTO)
                    .build();
        }).toList();
    }



}