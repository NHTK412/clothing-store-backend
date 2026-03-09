package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.promotion.PromotionCreateRequestDTO;
import com.example.clothingstore.dto.promotion.PromotionResponseDTO;
import com.example.clothingstore.service.PromotionService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    final private PromotionService promotionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<PromotionResponseDTO>> createPromotion(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @Valid @RequestBody PromotionCreateRequestDTO promotionCreateRequestDTO,
            HttpServletRequest request) {
            
        Integer adminId = userDetails.getUserId();


        PromotionResponseDTO promotionResponseDTO = promotionService.createPromotion(promotionCreateRequestDTO, adminId);

        return ResponseEntity.ok(
                ApiResponse.created("Successfully created promotion", promotionResponseDTO, request.getRequestURI()));
    }
}