package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.product.ProductRequestDTO;
import com.example.clothingstore.dto.product.ProductResponseDTO;
import com.example.clothingstore.dto.product.ProductSummaryDTO;
import com.example.clothingstore.model.Product;
import com.example.clothingstore.model.ProductColor;

@Mapper(componentModel = "spring", uses = {
        ProductColorMapper.class,
        ReviewMapper.class
})
public interface ProductMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Product toEntity(ProductRequestDTO productRequestDTO);

    @AfterMapping
    default void setParent(@MappingTarget Product product) {
        if (product.getProductColors() != null) {
            for (ProductColor color : product.getProductColors()) {

                // set cha cho color
                color.setProduct(product);

                // if (color.getProductDetails() != null) {
                // for (ProductDetail detail : color.getProductDetails()) {

                // // set cha cho detail
                // detail.setProductColor(color);
                // }
                // }
            }
        }

    }

    @Mapping(target = "averageRating", ignore = true)
    ProductResponseDTO toResponseDTO(Product product);

    @AfterMapping
    default void calculateAverageRating(@MappingTarget ProductResponseDTO productResponseDTO, Product product) {
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            double average = product.getReviews()
                    .stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
            productResponseDTO.setAverageRating(average);
        } else {
            productResponseDTO.setAverageRating(0.0);
        }
    }

    @Mapping(target = "averageRating", ignore = true)
    ProductSummaryDTO toSummaryDTO(Product product);

    @AfterMapping
    default void calculateAverageRatingSummary(@MappingTarget ProductSummaryDTO productSummaryDTO, Product product) {
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            double average = product.getReviews()
                    .stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
            productSummaryDTO.setAverageRating(average);
        } else {
            productSummaryDTO.setAverageRating(0.0);
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateEntityFromDTO(ProductRequestDTO productRequestDTO, @MappingTarget Product product);

}
