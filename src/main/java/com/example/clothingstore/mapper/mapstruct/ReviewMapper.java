package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.review.ReviewResponseDTO;
import com.example.clothingstore.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "userNameCustomer", ignore = true)
    ReviewResponseDTO toResponseDTO(Review review);

    @AfterMapping
    default void setUserNameCustomer(@MappingTarget ReviewResponseDTO reviewResponseDTO, Review review) {
        if (review.getCustomer() != null) {
            reviewResponseDTO.setUserNameCustomer(review.getCustomer().getUserName());
        }
    }

}