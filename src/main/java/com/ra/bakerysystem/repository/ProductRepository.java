package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {



    @Query("""
        SELECT p FROM Product p
        WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:isActive IS NULL OR p.active = :isActive)
    """)
    List<Product> findProducts(
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            @Param("isActive") Boolean isActive
    );

    boolean existsByName(String name);


    Optional<Product> findByName(String name);

    @Query("""
        SELECT p, i
        FROM Product p
        LEFT JOIN Inventory i ON i.product.id = p.id
        WHERE
            (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND
            (:categoryId IS NULL OR p.category.id = :categoryId)
        ORDER BY p.id
    """)
    List<Object[]> findProductsWithInventory(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId
    );
}
