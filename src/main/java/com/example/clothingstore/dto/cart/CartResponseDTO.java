package com.example.clothingstore.dto.cart;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {

    private Integer cartId;

    private List<CartItemResponseDTO> cartItem = new ArrayList<>();

}
