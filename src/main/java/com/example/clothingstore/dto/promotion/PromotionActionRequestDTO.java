package com.example.clothingstore.dto.promotion;

import java.util.Map;

import com.example.clothingstore.enums.PromotionActionTypeEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionActionRequestDTO {

    @NotNull(message = "Action type is required")
    private PromotionActionTypeEnum actionType;

    @NotNull(message = "Action value is required")
    private Map<String, Object> value;

    /**
     * Ví dụ:
     * 
     * PERCENT_DISCOUNT (Toàn đơn hàng):
     * {
     *   "discountPercentage": 10.0
     * }
     * 
     * FIXED_DISCOUNT (Toàn đơn hàng):
     * {
     *   "fixedDiscount": 50000.0
     * }
     * 
     * PRODUCT_PERCENT_DISCOUNT:
     * {
     *   "promotionGroupId": 1,
     *   "discountPercentage": 20.0
     * }
     * 
     * PRODUCT_FIXED_DISCOUNT:
     * {
     *   "promotionGroupId": 1,
     *   "fixedDiscount": 100000.0
     * }
     * 
     * FREE_PRODUCT:
     * {
     *   "freeProductId": 5,
     *   "quantity": 1
     * }
     */
}