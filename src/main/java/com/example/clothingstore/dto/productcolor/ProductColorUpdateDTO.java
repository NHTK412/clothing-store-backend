package com.example.clothingstore.dto.productcolor;

import java.util.List;

import com.example.clothingstore.dto.productdetail.ProductDetailRequestDTO;
import com.example.clothingstore.dto.productdetail.ProductDetailUpdateDTO;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductColorUpdateDTO {

    @NotNull(message = "Color ID is required")
    private Integer colorId;

    private String color;

    private String productImage;

    @Valid
    @NotEmpty(message = "Product details list cannot be empty")
    private List<ProductDetailUpdateDTO> productDetails;
}
