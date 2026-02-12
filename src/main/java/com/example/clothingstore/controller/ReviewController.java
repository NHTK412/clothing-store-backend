package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.review.ReviewRequestDTO;
import com.example.clothingstore.dto.review.ReviewResponseDTO;
import com.example.clothingstore.security.CustomerUserDetails;
import com.example.clothingstore.service.ReviewService;
import com.example.clothingstore.util.ApiResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/product/{productId}/reviews")
@RequiredArgsConstructor

public class ReviewController {

    // @Autowired
    // private ReviewService reviewService;

    private final ReviewService reviewService;

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponseDTO>>> getALLReviewByProductId(
            @PathVariable Integer productId, @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        List<ReviewResponseDTO> reviewResponseDTOs = reviewService.getALLReviewByProductId(productId, pageable);

        return ResponseEntity.ok(new ApiResponse<List<ReviewResponseDTO>>(true, null, reviewResponseDTOs));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PostMapping("/{orderdetailId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> createReviewByProductId(@PathVariable Integer orderdetailId,
            @PathVariable Integer productId,
            ReviewRequestDTO reviewRequestDTO, @AuthenticationPrincipal CustomerUserDetails userDetails) {

        ReviewResponseDTO reviewResponseDTO = reviewService.createReviewByProductId(orderdetailId,
                userDetails.getUserId(), productId,
                reviewRequestDTO);

        return ResponseEntity.ok(new ApiResponse<ReviewResponseDTO>(true, null, reviewResponseDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> updateReview(@PathVariable Integer productId,
            @PathVariable Integer reviewId,
            ReviewRequestDTO reviewRequestDTO) {

        ReviewResponseDTO reviewResponseDTO = reviewService.updateReview(productId, reviewId, reviewRequestDTO);

        return ResponseEntity.ok(new ApiResponse<ReviewResponseDTO>(true, null, reviewResponseDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDTO>> deleteReview(@PathVariable Integer productId,
            @PathVariable Integer reviewId) {

        ReviewResponseDTO reviewResponseDTO = reviewService.deleteReview(productId, reviewId);

        return ResponseEntity.ok(new ApiResponse<ReviewResponseDTO>(true, null, reviewResponseDTO));
    }

}
