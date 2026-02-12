package com.example.clothingstore.dto.productvariant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {

    private String size;

    private String color;

    private Integer stock;

    private String image;

    private Double unitPrice;

}
