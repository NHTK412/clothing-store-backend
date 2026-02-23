package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.promotion.PromotionCreateRequestDTO;
import com.example.clothingstore.dto.promotion.PromotionResponseDTO;
import com.example.clothingstore.service.PromotionService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/admin/promotion")
@RequiredArgsConstructor
public class PromotionController {

    final private PromotionService promotionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<PromotionResponseDTO>> createPromotion(
            @Valid @RequestBody PromotionCreateRequestDTO promotionCreateRequestDTO) {

        PromotionResponseDTO promotionResponseDTO = promotionService.createPromotion(promotionCreateRequestDTO);

        ApiResponse<PromotionResponseDTO> apiResponse = new ApiResponse<>(
                true,
                "Promotion created successfully",
                promotionResponseDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}