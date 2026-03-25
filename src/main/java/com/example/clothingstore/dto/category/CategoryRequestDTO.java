package com.example.clothingstore.dto.category;

import com.example.clothingstore.enums.CategoryStatusEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryRequestDTO {

    @NotNull(message = "Category name must not be null")
    private String categoryName;

    @NotNull(message = "Category name must not be null")
    private String description;

    @NotNull(message = "Category name must not be null")
    private CategoryStatusEnum status;
}
