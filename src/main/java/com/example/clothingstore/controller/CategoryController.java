package com.example.clothingstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.category.CategoryRequestDTO;
import com.example.clothingstore.dto.category.CategoryResponseDTO;
import com.example.clothingstore.dto.category.CategorySummaryDTO;
// import com.example.clothingstore.dto.category.CategorySummaryDTO;
import com.example.clothingstore.service.CategoryService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    // @Autowired
    // private CategoryService categoryService;

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategorySummaryDTO>>> getAllCategory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        List<CategorySummaryDTO> categorySummaryDTOs = categoryService.getAllCategory(pageable);

        return ResponseEntity.ok(new ApiResponse<List<CategorySummaryDTO>>(true, null, categorySummaryDTOs));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable Integer categoryId) {

        CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(new ApiResponse<CategoryResponseDTO>(true, null, categoryResponseDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {

        CategoryResponseDTO categoryResponseDTO = categoryService.createCategory(categoryRequestDTO);

        return ResponseEntity.ok(new ApiResponse<CategoryResponseDTO>(true, null, categoryResponseDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(@PathVariable Integer categoryId,
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {

        CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(categoryId, categoryRequestDTO);

        return ResponseEntity.ok(new ApiResponse<CategoryResponseDTO>(true, null, categoryResponseDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> deleteCategory(@PathVariable Integer categoryId) {

        CategoryResponseDTO categoryResponseDTO = categoryService.deleteCategory(categoryId);

        return ResponseEntity.ok(new ApiResponse<CategoryResponseDTO>(true, null, categoryResponseDTO));
    }

    // endpoint này dùng cho bên admin hiển thị danh sách các danh mục
    @GetMapping("/details")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategoriesDetailed(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        List<CategoryResponseDTO> categorySummaryDTOs = categoryService.getAllCategoriesDetailed(pageable);

        return ResponseEntity.ok(new ApiResponse<List<CategoryResponseDTO>>(true, null, categorySummaryDTOs));
    }

}
