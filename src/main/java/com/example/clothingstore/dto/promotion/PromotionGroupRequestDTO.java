package com.example.clothingstore.dto.promotion;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionGroupRequestDTO {

    @NotBlank(message = "Group name is required")
    private String groupName;

    private String description;

    @NotEmpty(message = "Products list cannot be empty")
    private List<Integer> productIds; // ID của các sản phẩm trong nhóm
}