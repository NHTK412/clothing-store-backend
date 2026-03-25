package com.example.clothingstore.mapper.mapstruct;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.promotion.PromotionGroupResponseDTO;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.PromotionGroup;

@Mapper(componentModel = "spring")
public interface PromotionGroupMapper {

    @Mapping(target = "productIds", ignore = true)
    PromotionGroupResponseDTO toResponseDTO(PromotionGroup promotionGroup);

    @AfterMapping
    default void mapProductIds(
            @MappingTarget PromotionGroupResponseDTO.PromotionGroupResponseDTOBuilder responseDTO,
            PromotionGroup promotionGroup) {

        List<Integer> productIds = null;

        if (promotionGroup.getProducts() != null && !promotionGroup.getProducts().isEmpty()) {
            productIds = promotionGroup.getProducts().stream()
                    .map(product -> product.getProductId())
                    .collect(Collectors.toList());
        } else {
            productIds = List.of();
        }

        // responseDTO.setProductIds(productIds);
        responseDTO.productIds(productIds);
        

    }

    // @Mapping(target = "productIds", source = "products", qualifiedByName =
    // "mapProductIds")
    // PromotionGroupResponseDTO toResponseDTO(PromotionGroup promotionGroup);

    // @org.mapstruct.Named("mapProductIds")
    // default List<Integer> mapProductIds(List<Product> products) {
    // if (products != null && !products.isEmpty()) {
    // return products.stream()
    // .map(Product::getProductId)
    // .collect(Collectors.toList());
    // }
    // return List.of();
    // }

}