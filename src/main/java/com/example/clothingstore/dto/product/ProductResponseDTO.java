package com.example.clothingstore.dto.product;

import java.util.List;

import com.example.clothingstore.dto.review.ReviewResponseDTO;
import com.example.clothingstore.enums.StatusEnum;
import com.example.clothingstore.model.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Integer productId;

    private String productName;

    private Double unitPrice;

    private Double discount;

    private String description;

    private String productImage;

    private List<ProductColorResponseDTO> productColors;

    private List<ReviewResponseDTO> reviews;

    private StatusEnum status;

    // Trung bình rating
    private Double averageRating;

    public ProductResponseDTO(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.unitPrice = product.getUnitPrice();
        this.discount = product.getDiscount();
        this.description = product.getDescription();
        this.productImage = product.getProductImage();
        this.productColors = product.getProductColors()
                .stream()
                .map((producrColor) -> {
                    return new ProductColorResponseDTO(producrColor);
                })
                .toList();

        this.reviews = product.getReviews()
                .stream()
                .map((review) -> {
                    return new ReviewResponseDTO(review);
                })
                .toList();

        this.averageRating = product.getReviews().stream()
                .mapToInt((review) -> review.getRating())
                .average()
                .orElse(0.0);

        this.status = product.getStatus();
    }
}
