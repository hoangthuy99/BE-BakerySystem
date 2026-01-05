package com.ra.bakerysystem.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ra.bakerysystem.common.ProductType;
import com.ra.bakerysystem.model.entity.Product;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {

    @JsonProperty("product_id")
    private Long id;

    private String name;
    private Integer price;
    private ProductType type;

    @JsonProperty("is_alcoholic")
    private Boolean alcoholic;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("is_active")
    private Boolean active;

    @JsonProperty("category_id")
    private Long categoryId;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.type = product.getType();
        this.alcoholic = product.getAlcoholic();
        this.imageUrl = product.getImageUrl();
        this.active = product.getActive();
        this.categoryId = product.getCategory() != null
                ? product.getCategory().getId()
                : null;
    }
}