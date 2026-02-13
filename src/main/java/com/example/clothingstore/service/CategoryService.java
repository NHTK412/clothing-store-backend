package com.example.clothingstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.category.CategoryRequestDTO;
import com.example.clothingstore.dto.category.CategoryResponseDTO;
import com.example.clothingstore.dto.category.CategorySummaryDTO;
import com.example.clothingstore.enums.CategoryStatusEnum;
import com.example.clothingstore.exception.customer.NotFoundException;
import com.example.clothingstore.mapper.mapstruct.CategoryMapper;
// import com.example.clothingstore.mapper.CategoryMapper;
import com.example.clothingstore.model.Category;
import com.example.clothingstore.repository.CategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    // @Autowired
    // private CategoryRepository categoryRepository;

    // @Autowired
    // private CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public List<CategorySummaryDTO> getAllCategory(Pageable pageable) {
        // Page<Category> categories = categoryRepository.findAll(pageable);
        Page<Category> categories = categoryRepository.findByStatus(CategoryStatusEnum.ACTIVE, pageable);

        // return categories.stream()
        // .map((category) ->
        // categoryMapper.convertCategoryToCategorySummaryDTO(category))
        // .toList();
        return categories.stream()
                .map(categoryMapper::toSummaryDTO)
                .toList();
    }

    @Transactional
    public CategoryResponseDTO getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Invalid category code"));

        // return categoryMapper.convertCategoryToCategoryResponseDTO(category);
        return categoryMapper.toResponseDTO(category);

    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category();

        // category = categoryMapper.convertCategoryRequestToModel(categoryRequestDTO,
        // category);
        category = categoryMapper.toEntity(categoryRequestDTO);

        categoryRepository.save(category);

        // return categoryMapper.convertCategoryToCategoryResponseDTO(category);
        return categoryMapper.toResponseDTO(category);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Integer categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Invalid category code"));

        // category = categoryMapper.convertCategoryRequestToModel(categoryRequestDTO,
        // category);
        // category = categoryMapper.toEntity(categoryRequestDTO);
        categoryMapper.updateEntityFromDTO(categoryRequestDTO, category);

        categoryRepository.save(category);

        // return categoryMapper.convertCategoryToCategoryResponseDTO(category);
        return categoryMapper.toResponseDTO(category);
    }

    @Transactional
    public CategoryResponseDTO deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Invalid category code"));
        // CategoryResponseDTO categoryResponseDTO =
        // categoryMapper.convertCategoryToCategoryResponseDTO(category);
        CategoryResponseDTO categoryResponseDTO = categoryMapper.toResponseDTO(category);

        categoryRepository.delete(category);

        return categoryResponseDTO;
    }

    @Transactional
    public List<CategoryResponseDTO> getAllCategoriesDetailed(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.stream()
                // .map((category) ->
                // categoryMapper.convertCategoryToCategoryResponseDTO(category))
                .map(categoryMapper::toResponseDTO)
                .toList();
    }

}
