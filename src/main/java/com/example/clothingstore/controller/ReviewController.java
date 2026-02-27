package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.review.ReviewRequestDTO;
import com.example.clothingstore.dto.review.ReviewResponseDTO;
import com.example.clothingstore.service.ReviewService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("v1/product/{productId}/reviews")
@RequiredArgsConstructor

public class ReviewController {

    // @Autowired
    // private ReviewService reviewService;

    private final ReviewService reviewService;

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponseDTO>>> getALLReviewByProductId(
            @PathVariable Integer productId, @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ReviewResponseDTO> reviewResponseDTOs = reviewService.getALLReviewByProductId(productId, pageable);

        // return ResponseEntity.ok(new ApiResponse<List<ReviewResponseDTO>>(true, null,
        // reviewResponseDTOs));
        return ResponseEntity.ok(
                ApiResponse.success("Successfully retrieved reviews for the product", reviewResponseDTOs,
                        request.getRequestURI()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    // @PostMapping("/{orderdetailId}")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReviewByProductId(
            ReviewRequestDTO reviewRequestDTO, @AuthenticationPrincipal CustomerUserDetails userDetails,
            HttpServletRequest request) {

        ReviewResponseDTO reviewResponseDTO = reviewService.createReviewByProductId(reviewRequestDTO.getProductId(),
                userDetails.getUserId(), reviewRequestDTO.getOrderdetailId(),
                reviewRequestDTO);

        // return ResponseEntity.ok(new ApiResponse<ReviewResponseDTO>(true, null,
        // reviewResponseDTO));
        return ResponseEntity
                .ok(ApiResponse.created("Successfully created review", reviewResponseDTO, request.getRequestURI()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(@PathVariable Integer productId,
            @PathVariable Integer reviewId,
            ReviewRequestDTO reviewRequestDTO,
            HttpServletRequest request) {

        ReviewResponseDTO reviewResponseDTO = reviewService.updateReview(productId, reviewId, reviewRequestDTO);

        // return ResponseEntity.ok(new ApiResponse<ReviewResponseDTO>(true, null,
        // reviewResponseDTO));
        return ResponseEntity
                .ok(ApiResponse.success("Successfully updated review", reviewResponseDTO, request.getRequestURI()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> deleteReview(@PathVariable Integer productId,
            @PathVariable Integer reviewId,
            HttpServletRequest request) {

        ReviewResponseDTO reviewResponseDTO = reviewService.deleteReview(productId, reviewId);

        // return ResponseEntity.ok(new ApiResponse<ReviewResponseDTO>(true, null,
        // reviewResponseDTO));
        return ResponseEntity
                .ok(ApiResponse.success("Successfully deleted review", reviewResponseDTO, request.getRequestURI()));
    }

}
