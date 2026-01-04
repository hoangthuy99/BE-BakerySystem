package com.ra.bakerysystem.model.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // ID của bảng trung gian => không cần gửi về Frontend
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore // Tránh vòng lặp vô tận khi render JSON
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(nullable = false)
    private String name; // Lưu tên sản phẩm tại thời điểm bán (Snapshot)

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    @JsonProperty("unit_price")
    private Integer unitPrice;

    // Trả về product_id dưới dạng String để khớp với types.ts
    @JsonGetter("product_id")
    public Long getProductId() {
        return product != null ? product.getId() : null;
    }

    // Helper cho Frontend nếu cần hiển thị ngay subtotal
    @JsonGetter("sub_total")
    public Integer getSubTotal() {
        if (unitPrice == null || quantity == null) {
            return 0;
        }
        return unitPrice * quantity;
    }
}