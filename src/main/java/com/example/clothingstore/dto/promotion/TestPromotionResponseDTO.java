package com.example.clothingstore.dto.promotion;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.PromotionTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPromotionResponseDTO {

    private Integer promotionId;

    private String promotionName;

    private PromotionTypeEnum promotionType;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
