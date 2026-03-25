package com.example.clothingstore.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {

    // private Integer productId;

    private Integer orderdetailId;

    private Integer rating;

    private String reviewContent;

}
