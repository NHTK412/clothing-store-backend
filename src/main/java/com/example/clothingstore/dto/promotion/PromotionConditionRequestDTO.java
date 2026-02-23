package com.example.clothingstore.dto.promotion;

import java.util.Map;

import com.example.clothingstore.enums.PromotionConditionTypeEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionConditionRequestDTO {

    @NotNull(message = "Condition type is required")
    private PromotionConditionTypeEnum conditionType;

    private String operator; // >=, <=, ==, etc.

    @NotNull(message = "Condition value is required")
    private Map<String, Object> value;

    /**
     * Ví dụ:
     * 
     * MIN_QUANTITY:
     * {
     * "productDetailIds": [1, 2, 3],
     * "minQuantity": 2
     * }
     * 
     * MIN_ORDER_AMOUNT:
     * {
     * "minOrderAmount": 500000.0
     * }
     * 
     * PRODUCT_SPECIFIC:
     * {
     * "productIds": [1, 2, 3]
     * }
     */
}