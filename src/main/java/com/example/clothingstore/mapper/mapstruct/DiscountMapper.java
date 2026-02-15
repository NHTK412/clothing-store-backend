package com.example.clothingstore.mapper.mapstruct;

import org.aspectj.lang.annotation.After;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.discount.DiscountRequestDTO;
import com.example.clothingstore.dto.discount.DiscountResponseDTO;
import com.example.clothingstore.model.Discount;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    DiscountResponseDTO toResponseDTO(Double discount);

    Discount toEntity(DiscountRequestDTO discountRequestDTO, Boolean type);

    @AfterMapping
    default void handleType(
            @MappingTarget Discount discount,
            boolean type) {
        if (!type) {
            discount.setDiscountPercentage(null);
        } else {
            discount.setDiscountAmount(null);
        }
    }
}