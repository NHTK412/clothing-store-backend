package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;

import com.example.clothingstore.dto.cartdetail.CartItemResponseDTO;
import com.example.clothingstore.model.CartItem;

@Mapper(componentModel = "spring")
public interface CartDetailMapper {

    CartItemResponseDTO toResponseDTO(CartItem cartItem);
}
