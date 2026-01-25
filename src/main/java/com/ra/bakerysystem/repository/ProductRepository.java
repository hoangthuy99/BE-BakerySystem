package com.ra.bakerysystem.repository;

import com.ra.bakerysystem.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByCategory_IdAndActiveTrue(Long categoryId);

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

    List<Product> findAllByActive(Boolean active);

//    @Query(value = """
//
//    """,nativeQuery=true)
//    List<Product> findAllByIds(List<Long> ids);

}
