package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.clothingstore.dto.gift.GiftRequestDTO;
import com.example.clothingstore.dto.gift.GiftResponseDTO;
import com.example.clothingstore.model.Gift;

@Mapper(componentModel = "spring")
public interface GiftMapper {

    @Mapping(source = "gift.productDetail.detailId", target = "productDetailId")
    @Mapping(source = "gift.productDetail.productColor.product.unitPrice", target = "unitPrice")
    @Mapping(source = "gift.productDetail.productColor.productImage", target = "productImage")
    @Mapping(source = "gift.productDetail.size", target = "productSize")
    @Mapping(source = "gift.productDetail.productColor.color", target = "productColor")
    @Mapping(source = "gift.productDetail.quantity", target = "productQuantity")
    GiftResponseDTO toResponseDTO(Gift gift);

    // Không quan tâm dến productDetailId
    
    Gift toEntity(GiftRequestDTO giftRequestDTO);

}