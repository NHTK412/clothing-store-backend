package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.productcolor.ProductColorRequestDTO;
import com.example.clothingstore.dto.productcolor.ProductColorResponseDTO;
import com.example.clothingstore.model.ProductColor;
import com.example.clothingstore.model.ProductDetail;

@Mapper(componentModel = "spring", uses = ProductDetailMapper.class)
public interface ProductColorMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "colorId", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductColor toEntity(ProductColorRequestDTO productColorRequestDTO);

    @AfterMapping
    default void setParent(@MappingTarget ProductColor productColor) {
        if (productColor.getProductDetails() != null) {
            for (ProductDetail detail : productColor.getProductDetails()) {

                // set cha cho detail
                detail.setProductColor(productColor);
            }
        }
    }

    ProductColorResponseDTO toResponseDTO(ProductColor productColor);

}