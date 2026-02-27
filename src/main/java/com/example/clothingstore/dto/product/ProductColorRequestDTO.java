package com.example.clothingstore.dto.product;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductColorRequestDTO {

    private String color;

    private String productImage;

    @Valid
    @NotEmpty(message = "Product details list cannot be empty")
    private List<ProductDetailRequestDTO> productDetails;
}
