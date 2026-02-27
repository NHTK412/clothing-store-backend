package com.example.clothingstore.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailPromotionDTO {

    private Integer detailId;

    private String size;

    private Integer stock;

}
