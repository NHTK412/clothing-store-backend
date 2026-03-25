package com.example.clothingstore.dto.product;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be a positive number")
    private Double unitPrice;

    private String description;

    private String productImage;

    @PositiveOrZero(message = "Discount must be zero or a positive number")
    private Double discount; // VD: 0.1 cho 10% giảm giá

    private List<Integer> categoryId; // Dùng để gắn danh mục

    @Valid
    @NotEmpty(message = "At least one product color is required")
    private List<ProductColorRequestDTO> productColors;

}
