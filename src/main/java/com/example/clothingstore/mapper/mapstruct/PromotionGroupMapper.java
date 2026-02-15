package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;

import com.example.clothingstore.dto.promotiongroup.PromotionGroupResponseDTO;
import com.example.clothingstore.model.PromotionGroup;

@Mapper(componentModel = "spring", uses = {
        ProductDetailMapper.class
        
})
public interface PromotionGroupMapper {

    PromotionGroupResponseDTO toResponseDTO(PromotionGroup promotionGroup);

}