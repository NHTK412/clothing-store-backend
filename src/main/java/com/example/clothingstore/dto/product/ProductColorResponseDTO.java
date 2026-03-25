package com.example.clothingstore.dto.product;

import java.util.List;

import com.example.clothingstore.enums.StatusEnum;
import com.example.clothingstore.model.ProductColor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductColorResponseDTO {

    private Integer colorId;

    private String color;

    private String productImage;

    private List<ProductDetailResponseDTO> productDetails;


    private StatusEnum status;

    public ProductColorResponseDTO(ProductColor productColor) {
        this.colorId = productColor.getColorId();
        this.color = productColor.getColor();
        this.productImage = productColor.getProductImage();
        this.status = productColor.getStatus();
        this.productDetails = productColor.getProductDetails()
                .stream()
                .map((productDetail) -> {
                    return new ProductDetailResponseDTO(productDetail);
                })
                .toList();
    }
}
