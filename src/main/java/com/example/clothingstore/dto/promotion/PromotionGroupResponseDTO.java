package com.example.clothingstore.dto.promotion;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionGroupResponseDTO {

    private Integer groupId;

    private String groupName;

    private String description;

    private List<Integer> productIds;
}