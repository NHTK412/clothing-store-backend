package com.example.clothingstore.dto.promotiongroup;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionGroupRequestDTO {

    @NotBlank(message = "Group name is required")
    private String groupName;

    private String description;

    @NotNull(message = "Minimum purchase quantity is required")
    @Positive(message = "Minimum purchase quantity must be a positive number")
    private Integer minPurchaseQuantity;

    @NotEmpty(message = "Product detail IDs list cannot be empty")
    private List<Integer> productDetailIds;
}
