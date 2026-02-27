package com.example.clothingstore.dto.cart;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCheckPromotionDTO {

    @NotEmpty(message = "Cart items must not be empty")
    private List<CartItemCheckPromotionDTO> cartItems;
}
