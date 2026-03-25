package com.example.clothingstore.mapper;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.cart.CartItemResponseDTO;
import com.example.clothingstore.model.CartItem;

@Component
public class CartDetailMapper {

    public CartItemResponseDTO convertModelToCartItemResponseDTO(CartItem cartItem) {
        CartItemResponseDTO cartItemResponseDTO = new CartItemResponseDTO();

        cartItemResponseDTO.setCartItemId(cartItem.getCartItemId());

        cartItemResponseDTO.setProductId(cartItem.getProductDetail().getProductColor().getProduct().getProductId());

        cartItemResponseDTO.setProductDetailId(cartItem.getProductDetail().getDetailId());

        cartItemResponseDTO.setProductColorId(cartItem.getProductDetail().getProductColor().getColorId());

        cartItemResponseDTO
                .setProductName(cartItem.getProductDetail().getProductColor().getProduct().getProductName());

        cartItemResponseDTO.setColor(cartItem.getProductDetail().getProductColor().getColor());

        cartItemResponseDTO.setProductDetailsize(cartItem.getProductDetail().getSize());

        cartItemResponseDTO.setProductImage(cartItem.getProductDetail().getProductColor().getProductImage());

        // cartDetailResponseDTO.setIsSelected(cartDetail.getIsSelected());

        cartItemResponseDTO.setQuantity(cartItem.getQuantity());

        cartItemResponseDTO.setPrice(cartItem.getProductDetail().getProductColor().getProduct().getUnitPrice());

        cartItemResponseDTO.setDiscount(cartItem.getProductDetail().getProductColor().getProduct().getDiscount());

        cartItemResponseDTO.setProductDetailQuantity(cartItem.getProductDetail().getQuantity());

        return cartItemResponseDTO;
    }

}
