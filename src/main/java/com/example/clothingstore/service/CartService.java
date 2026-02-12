package com.example.clothingstore.service;

import java.util.Optional;

// import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import com.example.clothingstore.dto.cart.CartRequestDTO;
import com.example.clothingstore.dto.cart.CartResponseDTO;
import com.example.clothingstore.dto.cartdetail.CartItemRequestDTO;
import com.example.clothingstore.dto.cartdetail.CartItemResponseDTO;
import com.example.clothingstore.exception.customer.AccessDeniedException;
import com.example.clothingstore.exception.customer.ConflictException;
import com.example.clothingstore.exception.customer.NotFoundException;
import com.example.clothingstore.mapper.CartDetailMapper;
import com.example.clothingstore.mapper.CartMapper;
import com.example.clothingstore.model.Cart;
import com.example.clothingstore.model.CartItem;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.repository.CartDetailRepository;
import com.example.clothingstore.repository.CartRepository;
import com.example.clothingstore.repository.ProductDetailRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    // @Autowired
    // private CartRepository cartRepository;

    // @Autowired
    // private CartDetailRepository cartDetailRepository;

    // @Autowired
    // private ProductDetailRepository productDetailRepository;

    // @Autowired
    // private CartMapper cartMapper;

    // @Autowired
    // private CartDetailMapper cartDetailMapper;

    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CartMapper cartMapper;
    private final CartDetailMapper cartDetailMapper;

    @Transactional
    public CartResponseDTO getCartByCustomer(Integer customerId) {
        Cart cart = cartRepository.findByCustomerIdWithALLFetch(customerId)
                .orElseThrow(() -> new NotFoundException("Invalue Cart By Customer"));

        return cartMapper.convertModelTOCartResponseDTO(cart);
    }

    @Transactional
    public CartItemResponseDTO addCartItemByCart(Integer customerId, CartItemRequestDTO cartItemRequestDTO) {

        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Invalid Cart By Customer"));

        ProductDetail productDetail = productDetailRepository.findById(cartItemRequestDTO.getProductDetailId())
                .orElseThrow(() -> new NotFoundException("Invalid Product Detail Code"));

        Optional<CartItem> optionalCartItem = cartDetailRepository
                .findByCart_CartIdAndProductDetail_DetailId(cart.getCartId(), productDetail.getDetailId());

        CartItem cartItem = optionalCartItem.orElse(null);

        // Nếu cartItem đã tồn tại, cộng dồn số lượng
        Integer quantity = (cartItem == null) ? cartItemRequestDTO.getQuantity()
                : cartItem.getQuantity() + cartItemRequestDTO.getQuantity();

        if (quantity > productDetail.getQuantity()) {
            throw new ConflictException("MAX QUANTITY");
        }

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProductDetail(productDetail);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
        } else {
            cartItem.setQuantity(quantity);
        }

        cartDetailRepository.save(cartItem);

        return cartDetailMapper.convertModelToCartItemResponseDTO(cartItem);
    }

    @Transactional
    public CartItemResponseDTO updateCartItem(Integer customerId, Integer cartDetailId, Integer quantity) {

        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new NotFoundException("Invalue Cart By Customer"));

        CartItem cartItem = cartDetailRepository.findByCartItemIdAndCart_CartId(cartDetailId, cart.getCartId())
                .orElseThrow(() -> new NotFoundException("Invalue"));

        CartItemResponseDTO cartItemResponseDTO = null;
        if (quantity != null) {
            if (quantity <= 0) {
                // cartItemResponseDTO =
                // cartDetailMapper.convertModelToCartItemResponseDTO(cartDetail);
                cartDetailRepository.delete(cartItem);
            } else {
                cartItem.setQuantity(quantity);
            }
        }
        // if (isSelected != null) {
        // cartDetail.setIsSelected(isSelected);
        // // cartItemResponseDTO =
        // // cartDetailMapper.convertModelToCartItemResponseDTO(cartDetail);

        // }
        cartItemResponseDTO = cartDetailMapper.convertModelToCartItemResponseDTO(cartItem);

        return cartItemResponseDTO;
    }

    @Transactional
    public CartItemResponseDTO deleteCartItem(Integer customerId, Integer cartDetailId) {

        CartItem cartDetail = cartDetailRepository
                .findById(cartDetailId)
                .orElseThrow(() -> new NotFoundException("Invalue CartDetail Code"));

        if (!cartDetail.getCart().getCustomer().getCustomerId().equals(customerId)) {
            throw new AccessDeniedException("You cannot delete items from another user's cart");
        }

        CartItemResponseDTO cartItemResponseDTO = cartDetailMapper.convertModelToCartItemResponseDTO(cartDetail);

        cartDetailRepository.delete(cartDetail);

        return cartItemResponseDTO;

    }

}
