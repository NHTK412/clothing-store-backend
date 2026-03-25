package com.example.clothingstore.dto.promotion;

import java.util.Map;

import com.example.clothingstore.enums.PromotionActionTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionActionResponseDTO {

    private Integer promotionActionId;

    private PromotionActionTypeEnum actionType;

    private Map<String, Object> value;
}