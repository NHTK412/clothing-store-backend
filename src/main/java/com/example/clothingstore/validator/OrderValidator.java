package com.example.clothingstore.validator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private Map<OrderStatusEnum, List<OrderStatusEnum>> allowedStatusTransitions = Map.of(
            OrderStatusEnum.PLACED, List.of(OrderStatusEnum.PREPARING, OrderStatusEnum.CANCELED),
            OrderStatusEnum.PREPARING, List.of(OrderStatusEnum.SHIPPED, OrderStatusEnum.CANCELED),
            OrderStatusEnum.SHIPPED, List.of(OrderStatusEnum.DELIVERED),
            OrderStatusEnum.DELIVERED, List.of(),
            OrderStatusEnum.CANCELED, List.of());

    private final OrderRepository orderRepository;

    public Order validateAndGetOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public Order validateAndGetOrderForCustomer(Integer orderId, Integer customerId) {
        return orderRepository.findByOrderIdAndCustomer_UserId(orderId, customerId)
                .orElseThrow(() -> new NotFoundException("Order not found for this customer"));
    }

    public boolean isValidStatusTransition(OrderStatusEnum currentStatus, OrderStatusEnum newStatus) {
        boolean isValid = allowedStatusTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus);
        if (!isValid) {
            throw new ConflictException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }
        return isValid;
    }

}
