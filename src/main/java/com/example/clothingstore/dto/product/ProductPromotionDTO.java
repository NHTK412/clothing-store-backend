package com.example.clothingstore.dto.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPromotionDTO {

    private Integer productId;

    private String productName;

    private Double productPrice;

    private List<ProductColorPromotionDTO> productcolors;

}
