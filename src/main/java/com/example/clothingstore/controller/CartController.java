package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.cart.CartItemRequestDTO;
import com.example.clothingstore.dto.cart.CartItemResponseDTO;
import com.example.clothingstore.dto.cart.CartResponseDTO;
import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.product.CreatePreviewDTO;
import com.example.clothingstore.service.CartService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@PreAuthorize("hasRole('CUSTOMER')")
@RestController
@RequestMapping("v1/customers/me/cart")
@RequiredArgsConstructor
public class CartController {

        // @Autowired
        // private CartService cartService;

        private final CartService cartService;

        @GetMapping
        public ResponseEntity<ApiResponse<CartResponseDTO>> getCartByCustomer(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        HttpServletRequest request) {
                // return new String();
                Integer customerId = userDetails.getUserId();

                CartResponseDTO cartResponseDTO = cartService.getCartByCustomer(customerId);

                // return ResponseEntity.ok(new ApiResponse<CartResponseDTO>(true, null,
                // cartResponseDTO));
                return ResponseEntity.ok(
                                ApiResponse.success("Successfully retrieved cart", cartResponseDTO,
                                                request.getRequestURI()));

        }

        @PostMapping("items")
        public ResponseEntity<ApiResponse<CartItemResponseDTO>> addCartItemByCart(
                        @Valid @RequestBody CartItemRequestDTO cartDetailRequestDTO,
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        HttpServletRequest request) {

                Integer customerId = userDetails.getUserId();

                CartItemResponseDTO cartItemResponseDTO = cartService.addCartItemByCart(customerId,
                                cartDetailRequestDTO);

                // return ResponseEntity.ok(new ApiResponse<CartItemResponseDTO>(true, null,
                // cartItemResponseDTO));
                return ResponseEntity.ok(
                                ApiResponse.created("Successfully added item to cart", cartItemResponseDTO,
                                                request.getRequestURI()));

        }

        @PatchMapping("items/{cartDetailId}")
        public ResponseEntity<ApiResponse<CartItemResponseDTO>> updateCartItem(
                        @PathVariable Integer cartDetailId,
                        @RequestParam(required = false) Integer quantity,
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        HttpServletRequest request) {

                Integer customerId = userDetails.getUserId();

                CartItemResponseDTO cartItemResponseDTO = cartService.updateCartItem(customerId, cartDetailId,
                                quantity);

                // return ResponseEntity.ok(new ApiResponse<CartItemResponseDTO>(true, null,
                // cartItemResponseDTO));
                return ResponseEntity.ok(
                                ApiResponse.success("Successfully updated cart item", cartItemResponseDTO,
                                                request.getRequestURI()));

        }

        @DeleteMapping("items/{cartDetailId}")
        public ResponseEntity<ApiResponse<CartItemResponseDTO>> deleteCartItem(
                        @PathVariable Integer cartDetailId, @AuthenticationPrincipal CustomerUserDetails userDetails,
                        HttpServletRequest request) {

                Integer customerId = userDetails.getUserId();

                CartItemResponseDTO cartItemResponseDTO = cartService.deleteCartItem(customerId, cartDetailId);

                // return ResponseEntity.ok(new ApiResponse<CartItemResponseDTO>(true, null,
                // cartItemResponseDTO));
                return ResponseEntity.ok(
                                ApiResponse.success("Successfully deleted cart item", cartItemResponseDTO,
                                                request.getRequestURI()));

        }

        // Xem trước đơn hàng
        @GetMapping("preview")
        public ResponseEntity<ApiResponse<OrderPreviewDTO>> previewOrder(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        // @RequestParam List<Integer> cartItemIds,
                        // @RequestParam(required = false) List<Integer> promotionIds,
                        @RequestBody CreatePreviewDTO createPreviewDTO,
                        HttpServletRequest request) {
                Integer customerId = userDetails.getUserId();

                // OrderPreviewDTO orderPreviewDTO = cartService.previewOrder(customerId, cartItemIds, promotionIds);
                OrderPreviewDTO orderPreviewDTO = cartService.previewOrder(customerId, createPreviewDTO);
                

                // return ResponseEntity.ok(new ApiResponse<OrderPreviewDTO>(true, null,
                // orderPreviewDTO));
                return ResponseEntity.ok(
                                ApiResponse.success("Successfully previewed order", orderPreviewDTO,
                                                request.getRequestURI()));
        }

}

// 1 GET /v1/cart Lấy toàn bộ giỏ hàng hiện tại
// 2 POST /v1/cart/items Thêm 1 sản phẩm vào giỏ
// 3 PATCH /v1/cart/items/{itemId} Cập nhật số lượng / chọn/bỏ chọn
// 4 DELETE /v1/cart/items/{itemId} Xóa 1 dòng
// 5 DELETE /v1/cart/items Xóa nhiều dòng (query param ids)
// 6 POST /v1/cart/clear Làm trống toàn bộ giỏ
// 7 POST /v1/cart/select-all Chọn tất cả item
// 8 POST /v1/cart/unselect-all Bỏ chọn tất cả item
// 9 POST /v1/cart/apply-voucher Áp dụng mã giảm giá
// 10 DELETE /v1/cart/voucher Gỡ mã giảm giá
// 11 GET /v1/cart/totals Lấy tổng tiền, giảm giá, phí ship ước lượng
// 12 POST /v1/cart/sync Đồng bộ giỏ guest → user khi login