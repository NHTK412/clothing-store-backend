package com.example.clothingstore.dto.cart;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {

    
    List<CartItemRequestDTO> cartDetailRequestDTOs;
}
