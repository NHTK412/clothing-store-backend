package com.example.clothingstore.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantGroupDTO {

    private String productName;

    private List<ProductVariantDTO> variants;
}
