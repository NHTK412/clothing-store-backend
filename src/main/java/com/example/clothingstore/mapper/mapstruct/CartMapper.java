package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;

import com.example.clothingstore.dto.cart.CartResponseDTO;
import com.example.clothingstore.model.Cart;

@Mapper(componentModel = "spring",
    uses = {
        CartDetailMapper.class
    }
)
public interface CartMapper {

    CartResponseDTO toResponseDTO(Cart cart);
}
