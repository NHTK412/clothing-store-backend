package com.example.clothingstore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.clothingstore.dto.order.OrderRequestDTO;
import com.example.clothingstore.dto.order.OrderResponseDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.security.CustomerUserDetails;
import com.example.clothingstore.service.OrderService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor

public class OrderController {

    // @Autowired
    // private OrderService orderService;

    private final OrderService orderService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {

        // Integer customerId = 1; // Temporary hardcoded customer ID for testing

        String userName = userDetails.getUsername();

        OrderResponseDTO createdOrder = orderService.createOrder(userName, orderRequestDTO);

        // ApiResponse<OrderResponseDTO> response = new
        // ApiResponse<OrderResponseDTO>(true, null, createdOrder);

        // return ResponseEntity.ok(response);

        return ResponseEntity.ok(
                ApiResponse.created("Successfully created order", createdOrder));
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable Integer orderId) {

        OrderResponseDTO orderResponseDTO = orderService.getOrderById(orderId);

        // ApiResponse<OrderResponseDTO> response = new ApiResponse<>(true, null,
        // orderResponseDTO);

        // return ResponseEntity.ok(response);

        return ResponseEntity.ok(
                ApiResponse.success("Successfully retrieved order", orderResponseDTO));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrderSummaryDTO>>> getAllOrderByCustomer(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Integer customerId = userDetails.getUserId();

        List<OrderSummaryDTO> orderSummaries = orderService.getAllOrdersByCustomer(customerId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Successfully retrieved orders", orderSummaries));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSummaryDTO>>> getAllOrder(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // return new String();

        Pageable pageable = PageRequest.of(page - 1, size);

        List<OrderSummaryDTO> orderSummaries = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(
                ApiResponse.success("Successfully retrieved orders", orderSummaries));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateStatus(@PathVariable Integer orderId,
            @RequestParam OrderStatusEnum status) {

        OrderResponseDTO updatedOrder = orderService.updateStatus(orderId, status);

        return ResponseEntity.ok(
                ApiResponse.success("Successfully updated order status", updatedOrder));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{orderId}/canceled")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateStatus(@PathVariable Integer orderId) {

        OrderResponseDTO updatedOrder = orderService.updateStatus(orderId, OrderStatusEnum.CANCELED);

        return ResponseEntity.ok(
                ApiResponse.success("Successfully canceled order", updatedOrder));

    }

}
