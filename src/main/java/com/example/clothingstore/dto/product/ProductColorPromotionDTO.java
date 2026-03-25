package com.example.clothingstore.dto.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductColorPromotionDTO {
    private Integer colorId;
    private String color;
    private String colorImage;

    private List<ProductDetailPromotionDTO> productDetails;

}
