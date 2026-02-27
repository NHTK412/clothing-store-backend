package com.example.clothingstore.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

    // Thông tin định danh
    private Integer cartItemId;
    private Integer productId;
    private Integer productDetailId;
    private Integer productColorId;

    // Thông tin sản phẩm
    private String productName;
    private String color;
    private String productDetailsize;
    private String productImage;

    // Thông tin giỏ hàng
    // private Boolean isSelected;
    private Integer quantity;
    private Double price;
    private Double discount; 

    // private Double price;

    // Thông tin tồn kho
    private Integer productDetailQuantity;
}
