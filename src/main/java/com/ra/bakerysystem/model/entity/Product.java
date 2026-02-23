package com.ra.bakerysystem.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ra.bakerysystem.common.ProductType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType type;

    @Column(name = "is_alcoholic")
    private Boolean alcoholic = false;

    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(name = "is_active")
    private Boolean active = true;
    @Column (name = "qty")
    private  Integer qty;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToOne(mappedBy = "product")
    @JsonIgnore
    private Inventory inventory;


    public static String generateOrderCode() {
        return "PD" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
    }

}
