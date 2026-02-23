package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.promotion.PromotionGroupRequestDTO;
import com.example.clothingstore.dto.promotion.PromotionGroupResponseDTO;
import com.example.clothingstore.service.PromotionGroupService;
import com.example.clothingstore.util.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/admin/promotion-groups")
@RequiredArgsConstructor
public class PromotionGroupController {

        private final PromotionGroupService promotionGroupService;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<PromotionGroupResponseDTO>> createPromotionGroup(
                        @RequestBody PromotionGroupRequestDTO promotionGroupRequest) {

                PromotionGroupResponseDTO createdPromotionGroup = promotionGroupService
                                .createPromotionGroup(promotionGroupRequest);

                ApiResponse<PromotionGroupResponseDTO> response = new ApiResponse<>(true,
                                "Promotion group created successfully", createdPromotionGroup);

                return ResponseEntity.ok(response);

        }

        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<PromotionGroupResponseDTO>> getPromotionGroupById(
                        @RequestParam Integer id) {

                PromotionGroupResponseDTO promotionGroupResponseDTO = promotionGroupService
                                .getPromotionGroupById(id);

                ApiResponse<PromotionGroupResponseDTO> response = new ApiResponse<>(true,
                                "Promotion group retrieved successfully", promotionGroupResponseDTO);

                return ResponseEntity.ok(response);

        }

}
