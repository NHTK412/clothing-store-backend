package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Bean;

import com.example.clothingstore.dto.productdetail.ProductDetailRequestDTO;
import com.example.clothingstore.dto.productdetail.ProductDetailResponseDTO;
import com.example.clothingstore.model.ProductDetail;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "detailId", ignore = true)
    // @Mapping(target = "gits", ignore = true)
    // @Mapping(target = "promotionGroups", ignore = true)
    @Mapping(target = "productColor", ignore = true)
    ProductDetail toEntity(ProductDetailRequestDTO productDetailRequestDTO);

    ProductDetailResponseDTO toResponseDTO(ProductDetail productDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "detailId", ignore = true)
    // @Mapping(target = "gits", ignore = true)
    // @Mapping(target = "promotionGroups", ignore = true)
    @Mapping(target = "productColor", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDTO(ProductDetailRequestDTO productDetailRequestDTO,
            @MappingTarget ProductDetail productDetail);

}