package com.example.clothingstore.dto.discount;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequestDTO {

    @Positive(message = "Discount percentage must be positive")
    private Double discountPercentage; // % giảm giá

    @Positive(message = "Discount percentage must be positive")
    private Double discountAmount; // số tiền giảm giá

    @Positive(message = "Discount percentage must be positive")
    private Double maxDiscount; // số tiền giảm tối đa

}
