//package com.ra.bakerysystem.repository;
//
//import com.ra.bakerysystem.common.AverageType;
//import com.ra.bakerysystem.model.entity.SaleAverageCalculateEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//public interface SalesAverageCalculateRepository extends JpaRepository<SaleAverageCalculateEntity, Long> {
//    @Query(
//        value = """
//        SELECT *
//        FROM sales_average_calculate
//        WHERE product_id = :productId
//          AND avg_type = :avgType
//          AND effective_date <= :date
//        ORDER BY effective_date DESC
//        LIMIT 1
//    """,
//        nativeQuery = true
//    )
//    Optional<SaleAverageCalculateEntity> findLatestAverageByDate(
//        @Param("productId") Long productId,
//        @Param("avgType") String avgType,
//        @Param("date") LocalDate date
//    );
//}
//
