package com.example.clothingstore.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.order.OrderRequestDTO;
import com.example.clothingstore.dto.order.OrderResponseDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.dto.product.CreatePreviewDTO;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.service.OrderService;
import com.example.clothingstore.util.ApiResponse;
import com.example.clothingstore.util.CustomerUserDetails;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("v1/orders")
@RequiredArgsConstructor

public class OrderController {

        private final OrderService orderService;

        @PreAuthorize("hasRole('CUSTOMER')")
        @PostMapping
        public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @Valid @RequestBody OrderRequestDTO orderRequestDTO,
                        HttpServletRequest request) {
                String userName = userDetails.getUsername();
                OrderResponseDTO createdOrder = orderService.createOrder(userName, orderRequestDTO);
                return ResponseEntity.ok(
                                ApiResponse.created(
                                                "Successfully created order",
                                                createdOrder,
                                                request.getRequestURI()));
        }

        @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
        @GetMapping("/{orderId}")
        public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @PathVariable Integer orderId,
                        HttpServletRequest request) {
                OrderResponseDTO orderResponseDTO = orderService.getOrderById(orderId, userDetails.getUserId());
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Successfully retrieved order",
                                                orderResponseDTO,
                                                request.getRequestURI()));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<Page<OrderSummaryDTO>>> getAllOrderByCustomer(
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size,
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        HttpServletRequest request) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Integer customerId = userDetails.getUserId();
                Page<OrderSummaryDTO> orderSummaries = orderService.getAllOrdersByCustomer(customerId, pageable);
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Successfully retrieved orders",
                                                orderSummaries,
                                                request.getRequestURI()));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping
        public ResponseEntity<ApiResponse<Page<OrderSummaryDTO>>> getAllOrder(
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer size,
                        HttpServletRequest request) {
                Pageable pageable = PageRequest.of(page - 1, size);
                Page<OrderSummaryDTO> orderSummaries = orderService.getAllOrders(pageable);
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Successfully retrieved orders",
                                                orderSummaries,
                                                request.getRequestURI()));
        }

        @PreAuthorize("hasRole('CUSTOMER')")
        @PatchMapping("/{orderId}/canceled")
        public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        @PathVariable Integer orderId,
                        HttpServletRequest request) {
                Integer customerId = userDetails.getUserId();
                OrderResponseDTO updatedOrder = orderService.cancelOrder(
                                customerId,
                                orderId);
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Successfully canceled order",
                                                updatedOrder,
                                                request.getRequestURI()));
        }

        @PreAuthorize("hasRole('ADMIN')")
        @PatchMapping("/{orderId}/status")
        public ResponseEntity<ApiResponse<OrderResponseDTO>> updateStatus(
                        @PathVariable Integer orderId,
                        @RequestParam OrderStatusEnum status,
                        HttpServletRequest request) {
                OrderResponseDTO updatedOrder = orderService.updateStatus(orderId, status);
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Successfully updated order status",
                                                updatedOrder,
                                                request.getRequestURI()));
        }

        @PostMapping("preview")
        public ResponseEntity<ApiResponse<OrderPreviewDTO>> previewOrder(
                        @AuthenticationPrincipal CustomerUserDetails userDetails,
                        // @RequestParam List<Integer> cartItemIds,
                        // @RequestParam(required = false) List<Integer> promotionIds,
                        @RequestBody CreatePreviewDTO createPreviewDTO,
                        HttpServletRequest request) {
                Integer customerId = userDetails.getUserId();

                // OrderPreviewDTO orderPreviewDTO = cartService.previewOrder(customerId,
                // cartItemIds, promotionIds);
                OrderPreviewDTO orderPreviewDTO = orderService.createPreviewOrder_v2(customerId, createPreviewDTO);

                // return ResponseEntity.ok(new ApiResponse<OrderPreviewDTO>(true, null,
                // orderPreviewDTO));
                return ResponseEntity.ok(
                                ApiResponse.success("Successfully previewed order", orderPreviewDTO,
                                                request.getRequestURI()));
        }

}
