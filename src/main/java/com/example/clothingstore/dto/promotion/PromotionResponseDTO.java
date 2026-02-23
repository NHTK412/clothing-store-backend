package com.example.clothingstore.dto.promotion;

import java.time.LocalDateTime;
import java.util.List;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.enums.PromotionTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionResponseDTO {

    private Integer promotionId;

    private String promotionName;

    private String description;

    private PromotionTypeEnum promotionType;

    private Integer priority;

    private Boolean isActive;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean stackable;

    private String couponCode;

    private Integer usageLimit;

    private PromotionScopeTypeEnum promotionScopeType;

    private List<PromotionConditionResponseDTO> conditions;

    private List<PromotionActionResponseDTO> actions;

    private List<PromotionGroupResponseDTO> promotionGroups;
}