//package com.ra.bakerysystem.model.entity;
//
//import com.ra.bakerysystem.common.AverageType;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "sales_average_calculate")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class SaleAverageCalculateEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "product_id", nullable = false)
//    private Long productId;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "avg_type", nullable = false, length = 10)
//    private AverageType avgType;
//
//    /**
//     * Ngày bắt đầu áp dụng giá trị trung bình
//     * VD: snapshot ngày 01/10 thì effective_date = 2024-10-01
//     */
//    @Column(name = "effective_date", nullable = false)
//    private LocalDate effectiveDate;
//
//    /**
//     * Tổng số lượng bán tích lũy
//     * VD: tổng sales 3 ngày = 30
//     */
//    @Column(name = "total_quantity", nullable = false)
//    private Integer totalQuantity;
//
//    /**
//     * Tổng số đơn vị thời gian
//     * VD: 3 ngày / 1 tháng / 1 năm
//     */
//    @Column(name = "total_units", nullable = false)
//    private Integer totalUnits;
//
//    /**
//     * Giá trị trung bình = total_quantity / total_units
//     */
//    @Column(name = "average_quantity", nullable = false, precision = 10, scale = 2)
//    private Integer averageQuantity;
//
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//}
