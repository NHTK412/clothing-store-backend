package com.example.clothingstore.dto.product;

import java.io.ObjectInputFilter.Status;

import com.example.clothingstore.enums.StatusEnum;
import com.example.clothingstore.model.ProductDetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDTO {

    private Integer detailId;

    // private String color;

    private String size;

    private Integer quantity;

    private StatusEnum status;

    // private String productImage;

    public ProductDetailResponseDTO(ProductDetail productDetail) {
        this.detailId = productDetail.getDetailId();
        // this.color = productDetail.getColor();
        this.size = productDetail.getSize();
        this.quantity = productDetail.getQuantity();
        // this.productImage = productDetail.getProductImage();

        this.status = productDetail.getStatus();
    }

}
