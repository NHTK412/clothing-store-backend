package com.example.clothingstore.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartItemCheckPromotionDTO {
    private Integer productDetailId;
    private Integer quantity;
}
