package com.example.clothingstore.dto.review;

import com.example.clothingstore.model.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {

    private Integer reviewId;

    private Integer rating;

    private String reviewContent;

    private String userNameCustomer;

    public ReviewResponseDTO(Review review) {
        this.reviewId = review.getReviewId();
        this.rating = review.getRating();
        this.reviewContent = review.getReviewContent();
        // this.userNameCustomer = review.getCustomer().getUserName();
        this.userNameCustomer = review.getCustomer().getUserName();
    }
}
