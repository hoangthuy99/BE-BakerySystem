package com.ra.bakerysystem.model.DTO;

import com.ra.bakerysystem.common.ProductType;
import com.ra.bakerysystem.model.entity.Product;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;

    private String name;
    private Integer price;
    private ProductType type;
    private Boolean alcoholic;
    private Integer qty;
    private String imageUrl;
    private Boolean isActive = true;
    private Long categoryId;


    public ProductDTO(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.type = product.getType();
        this.qty = product.getQty();
        this.alcoholic = product.getAlcoholic();
        this.imageUrl = product.getImageUrl();
        this.isActive = product.getActive();
        this.categoryId = product.getCategory().getId();
    }
}