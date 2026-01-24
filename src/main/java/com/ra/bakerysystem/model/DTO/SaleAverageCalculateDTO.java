//package com.ra.bakerysystem.model.DTO;
//
//import com.ra.bakerysystem.common.AverageType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class SaleAverageCalculateDTO {
//
//    private Long id;
//
//    private Long productId;
//
//    @Enumerated(EnumType.STRING)
//    private AverageType avgType;
//
//    /**
//     * Ngày bắt đầu áp dụng giá trị trung bình
//     * VD: snapshot ngày 01/10 thì effective_date = 2024-10-01
//     */
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
//    private Integer totalUnits;
//
//    /**
//     * Giá trị trung bình = total_quantity / total_units
//     */
//    private Integer averageQuantity;
//
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
//}
