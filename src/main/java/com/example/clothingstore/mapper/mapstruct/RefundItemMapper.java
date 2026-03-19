package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.clothingstore.dto.refund.RefundItemResponseDTO;
import com.example.clothingstore.model.RefundItem;

@Mapper(componentModel = "spring")
public interface RefundItemMapper {

    @Mapping(target = "productId", source = "refundItem.productDetail.productColor.product.productId")
    @Mapping(target = "productName", source = "refundItem.productDetail.productColor.product.productName")
    @Mapping(target = "productImageUrl", source = "refundItem.productDetail.productColor.productImage")
    @Mapping(target = "size", source = "refundItem.productDetail.size")
    @Mapping(target = "color", source = "refundItem.productDetail.productColor.color")
    RefundItemResponseDTO toResponseDTO(RefundItem refundItem);

}
