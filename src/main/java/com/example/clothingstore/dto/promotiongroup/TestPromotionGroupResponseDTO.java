package com.example.clothingstore.dto.promotiongroup;

import java.util.List;

import com.example.clothingstore.dto.product.ProductPromotionDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestPromotionGroupResponseDTO {

    private Integer groupId;

    private String groupName;

    private String description;

    private Integer minPurchaseQuantity;

    private List<ProductPromotionDTO> productPromotionDTOs;

}
