package com.example.clothingstore.dto.promotion;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionSummaryDTO {

    private Integer promotionId;

    private String promotionName;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean stackable;

    private Integer usageLimit;

}
