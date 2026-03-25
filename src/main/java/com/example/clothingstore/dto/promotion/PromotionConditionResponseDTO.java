package com.example.clothingstore.dto.promotion;

import java.util.Map;

import com.example.clothingstore.enums.PromotionConditionTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionConditionResponseDTO {

    private Integer promotionConditionId;

    private PromotionConditionTypeEnum conditionType;

    private String operator;

    private Map<String, Object> value;
}