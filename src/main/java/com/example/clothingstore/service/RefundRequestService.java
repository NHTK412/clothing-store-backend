package com.example.clothingstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.refund.RefundRequestDTO;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.exception.security.AccessDeniedHandlerException;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.model.OrderDetail;
import com.example.clothingstore.model.RefundItem;
import com.example.clothingstore.model.RefundPayment;
import com.example.clothingstore.model.RefundRequest;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.OrderRepository;
import com.example.clothingstore.repository.RefundRequestRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundRequestService {
        private final RefundRequestRepository refundRequestRepository;
        private final OrderRepository orderRepository;

        @Transactional
        public Map<String, Object> createRefundRequest(
                        Integer customerId,
                        RefundRequestDTO refundRequestDTO) {

                Order order = orderRepository.findById(refundRequestDTO.getOrderId())
                                .orElseThrow(() -> new NotFoundException(
                                                "Order not found with ID: " + refundRequestDTO.getOrderId()));

                if (!order.getCustomer().getUserId().equals(customerId)) {
                        throw new ConflictException(
                                        "Customer does not own the order with ID: " + refundRequestDTO.getOrderId());
                }

                if (order.getStatus() != OrderStatusEnum.DELIVERED) {
                        throw new ConflictException(
                                        "Refund request can only be created for orders with status DELIVERED.");
                }

                order.setStatus(OrderStatusEnum.RETURNED);
                orderRepository.save(order);

                Map<Integer, OrderDetail> orderDetailMaps = order.getOrderDetails().stream()
                                .collect(Collectors.toMap(
                                                (orderDetail) -> orderDetail.getDetailId(),
                                                (orderDetail) -> orderDetail));

                Map<Integer, Integer> orderItemIdsInRefundRequest = refundRequestDTO.getRefundItems().stream()
                                .collect(Collectors.toMap(
                                                (refundItem) -> refundItem.getOrderItemId(),
                                                (refundItem) -> refundItem.getQuantity()));

                boolean allItemsInRefundRequestExistInOrder = refundRequestDTO.getRefundItems()
                                .stream()
                                .allMatch(refundItem -> orderDetailMaps.containsKey(refundItem.getOrderItemId())
                                                && refundItem.getQuantity() > 0);

                if (!allItemsInRefundRequestExistInOrder) {
                        throw new ConflictException(
                                        "One or more items in the refund request do not exist in the order or have invalid quantity.");
                }

                RefundRequest refundRequest = new RefundRequest();

                refundRequest.setOrder(order);
                refundRequest.setReason(refundRequestDTO.getReason());

                boolean isFullOrderItemRefund = order.getOrderDetails()
                                .stream()
                                .allMatch(
                                                (orderDetail) -> {
                                                        return orderItemIdsInRefundRequest
                                                                        .containsKey(orderDetail.getDetailId())
                                                                        && orderDetail
                                                                                        .getQuantity() == orderItemIdsInRefundRequest
                                                                                                        .get(orderDetail.getDetailId());
                                                });

                BigDecimal refundShippingFee = isFullOrderItemRefund
                                ? BigDecimal.valueOf(order.getShippingFee() - order.getDiscountShippingFee())
                                : BigDecimal.ZERO;
                refundRequest.setRefundShippingFee(refundShippingFee);

                List<RefundItem> refundItems = refundRequestDTO.getRefundItems()
                                .stream()
                                .map(
                                                (refundItem) -> {
                                                        RefundItem item = new RefundItem();
                                                        item.setProductDetail(
                                                                        orderDetailMaps.get(refundItem.getOrderItemId())
                                                                                        .getProductDetail());
                                                        item.setQuantity(refundItem.getQuantity());
                                                        item.setRefundAmount(
                                                                        BigDecimal.valueOf(
                                                                                        orderDetailMaps.get(
                                                                                                        refundItem.getOrderItemId())
                                                                                                        .getFinalPrice()));
                                                        item.setRefundRequest(refundRequest);
                                                        return item;
                                                })
                                .collect(Collectors.toList());
                refundRequest.setRefundItems(refundItems);

                refundRequest.setRefundMethod(refundRequestDTO.getRefundMethod());
                refundRequest.setStatus(com.example.clothingstore.enums.RefundRequestStatusEnum.PENDING);

                refundRequestRepository.save(refundRequest);

                return Map.of(
                                "refundRequestId", refundRequest.getRefundRequestId());

        }

}