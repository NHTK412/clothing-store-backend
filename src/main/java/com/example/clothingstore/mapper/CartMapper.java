package com.example.clothingstore.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.cart.CartResponseDTO;
import com.example.clothingstore.dto.cartdetail.CartItemResponseDTO;
// import com.example.clothingstore.dto.cartdetail.CartDetailResponseDTO;
import com.example.clothingstore.model.Cart;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class CartMapper {

    // @Autowired
    // private CartDetailMapper cartDetailMapper;

    private final CartDetailMapper cartDetailMapper;

    public CartResponseDTO convertModelTOCartResponseDTO(Cart cart) {
        CartResponseDTO cartResponseDTO = new CartResponseDTO();

        cartResponseDTO.setCartId(cart.getCartId());

        List<CartItemResponseDTO> cartItemResponseDTOs = cart.getCartItems()
                .stream()
                .map((cartItemResponseDTO) -> cartDetailMapper
                        .convertModelToCartItemResponseDTO(cartItemResponseDTO))
                .toList();

        cartResponseDTO.setCartItemResponseDTOs(cartItemResponseDTOs);

        return cartResponseDTO;
    }

}
