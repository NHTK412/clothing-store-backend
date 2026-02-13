package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.category.CategoryRequestDTO;
import com.example.clothingstore.dto.category.CategoryResponseDTO;
import com.example.clothingstore.dto.category.CategorySummaryDTO;
import com.example.clothingstore.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // ignore là để tránh lỗi biên dịch do không tìm thấy trường tương ứng
    CategoryResponseDTO toResponseDTO(Category category);

    @Mapping(target = "productCount", ignore = true)
    CategorySummaryDTO toSummaryDTO(Category category);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequestDTO categoryRequestDTO);

    @AfterMapping
    default void setProductCount(Category category, @MappingTarget CategorySummaryDTO categorySummaryDTO) {
        categorySummaryDTO.setProductCount(category.getProducts().size());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromDTO(CategoryRequestDTO categoryRequestDTO, @MappingTarget Category category);

}