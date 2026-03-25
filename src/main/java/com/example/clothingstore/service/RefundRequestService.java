package com.example.clothingstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.refund.AddRefundPaymentDTO;
import com.example.clothingstore.dto.refund.CreateRefundPaymentDTO;
import com.example.clothingstore.dto.refund.RefundRequestDTO;
import com.example.clothingstore.dto.refund.RefundResponseDTO;
import com.example.clothingstore.dto.refund.RefundSummaryDTO;
import com.example.clothingstore.dto.refund.UpdateRefundRequest;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.enums.RefundMethodEnum;
import com.example.clothingstore.enums.RefundRequestStatusEnum;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.mapstruct.RefundMapper;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.model.OrderDetail;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.model.RefundItem;
import com.example.clothingstore.model.RefundPayment;
import com.example.clothingstore.model.RefundRequest;
import com.example.clothingstore.repository.OrderRepository;
import com.example.clothingstore.repository.RefundRequestRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefundRequestService {
        private final RefundRequestRepository refundRequestRepository;
        private final OrderRepository orderRepository;

        private final RefundMapper refundMapper;

        private static final Logger logger = LoggerFactory.getLogger(RefundRequestService.class);

        @Transactional
        // public void createRefundRequest_v2(
        public RefundResponseDTO createRefundRequest_v2(
                        Integer customerId,
                        RefundRequestDTO refundRequestDTO) {

                // Kiểm tra tồn tại
                Order order = orderRepository.findById(refundRequestDTO.getOrderId())
                                .orElseThrow(() -> new NotFoundException(
                                                "Order not found with ID: " + refundRequestDTO.getOrderId()));
                // Kiểm tra quyền sở hữu
                if (!order.getCustomer().getUserId().equals(customerId)) {
                        throw new ConflictException(
                                        "Customer does not own the order with ID: " + refundRequestDTO.getOrderId());
                }

                // Kiểm tra các item có thuộc đơn hàng không
                if (!refundRequestDTO.getRefundItems().stream()
                                .allMatch(refundItem -> order.getOrderDetails().stream()
                                                .anyMatch(orderDetail -> orderDetail.getDetailId()
                                                                .equals(refundItem.getOrderItemId())))) {
                        throw new ConflictException(
                                        "One or more items in the refund request do not exist in the order.");
                }
                // Kiểm tra trạng thái đơn hàng
                if (order.getStatus() != OrderStatusEnum.DELIVERED) {
                        throw new ConflictException(
                                        "Refund request can only be created for orders with status DELIVERED.");
                }
                // Kiểm tra item của request
                if (refundRequestDTO.getRefundItems() == null || refundRequestDTO.getRefundItems().isEmpty()) {
                        throw new ConflictException("Refund request must contain at least one item.");
                }

                Map<Integer, Integer> soSanPhamDaMua = order.getOrderDetails().stream()
                                .collect(Collectors.toMap(
                                                (orderDetail) -> orderDetail.getProductDetail().getDetailId(),
                                                (orderDetail) -> orderDetail.getQuantity()));

                for (Integer i : soSanPhamDaMua.keySet()) {
                        logger.warn("soSanPhamDaMua: " + i + " - " + soSanPhamDaMua.get(i));
                }

                Map<Integer, Integer> soSanPhamDaHoan = order
                                .getRefundRequests()
                                .stream()
                                .filter((refundRequest) -> refundRequest.getStatus() != RefundRequestStatusEnum.CANCEL
                                                &&
                                                refundRequest.getStatus() != RefundRequestStatusEnum.REJECTED)
                                .flatMap((refundRequest) -> refundRequest.getRefundItems().stream())
                                .collect(Collectors.groupingBy(
                                                (refundItem) -> refundItem.getProductDetail()
                                                                .getDetailId(),
                                                Collectors.summingInt(RefundItem::getQuantity)));
                for (Integer i : soSanPhamDaHoan.keySet()) {
                        logger.warn("soSanPhamDaHoan: " + i + " - " + soSanPhamDaHoan.get(i));
                }

                Map<Integer, Integer> sanPhamConLaiCoTheHoan = order.getOrderDetails().stream().collect(
                                Collectors.toMap((orderDetail) -> orderDetail.getProductDetail().getDetailId(),
                                                (orderDetail) -> {
                                                        Integer quantityMua = orderDetail.getQuantity();
                                                        Integer quantityDaHoan = soSanPhamDaHoan.get(orderDetail
                                                                        .getProductDetail().getDetailId()) == null
                                                                                        ? 0
                                                                                        : soSanPhamDaHoan.get(
                                                                                                        orderDetail.getProductDetail()
                                                                                                                        .getDetailId());
                                                        Integer quantityConLai = quantityMua - quantityDaHoan;
                                                        return quantityConLai;
                                                }));

                for (Integer i : sanPhamConLaiCoTheHoan.keySet()) {
                        logger.warn("sanPhamConLaiCoTheHoan: " + i + " - " + sanPhamConLaiCoTheHoan.get(i));
                }

                List<Integer> cacChiTieuTraHang = refundRequestDTO.getRefundItems().stream()
                                .map((refundItem) -> refundItem.getOrderItemId())
                                .collect(Collectors.toList());

                Map<Integer, OrderDetail> orderItemMaps = order.getOrderDetails().stream()
                                .filter((orderDetail) -> cacChiTieuTraHang.contains(orderDetail.getDetailId()))
                                .collect(Collectors.toMap(
                                                (orderDetail) -> orderDetail.getDetailId(),
                                                (orderDetail) -> orderDetail));

                Map<Integer, ProductDetail> productDetailMaps = order.getOrderDetails().stream()
                                .filter((orderDetail) -> cacChiTieuTraHang.contains(orderDetail.getDetailId()))
                                .collect(Collectors.toMap(
                                                (orderDetail) -> orderDetail.getDetailId(),
                                                (orderDetail) -> orderDetail.getProductDetail()));

                var soLuongYeuCauHoanTrongRequest = refundRequestDTO.getRefundItems().stream()
                                .collect(Collectors.toMap(
                                                (refundItem) -> orderItemMaps.get(refundItem.getOrderItemId())
                                                                .getProductDetail().getDetailId(),
                                                (refundItem) -> refundItem.getQuantity()));

                var allItemsInRefundRequestExistInOrder = soLuongYeuCauHoanTrongRequest.entrySet().stream()
                                .allMatch((entry) -> sanPhamConLaiCoTheHoan.containsKey(entry.getKey())
                                                && entry.getValue() > 0
                                                && entry.getValue() <= sanPhamConLaiCoTheHoan.get(entry.getKey()));
                if (!allItemsInRefundRequestExistInOrder) {
                        throw new ConflictException(
                                        "One or more items in the refund request do not exist in the order or have invalid quantity.");
                }

                // Hoàn tiền ship nếu như khách hàng yêu cầu hoàn tất cả sản phẩm của đơn hàng (
                // Nếu số khách yêu cầu = số lượng sản phẩm còn lại có thể hoàn trong đơn hàng)
                boolean isRefundShippingFee = soLuongYeuCauHoanTrongRequest.entrySet().stream()
                                .allMatch((entry) -> entry.getValue()
                                                .equals(sanPhamConLaiCoTheHoan.get(entry.getKey())));

                BigDecimal refundShippingFee = isRefundShippingFee
                                ? BigDecimal.valueOf(order.getShippingFee() - order.getDiscountShippingFee())
                                : BigDecimal.ZERO;

                // Tạo refund request
                RefundRequest newRefundRequest = new RefundRequest();
                newRefundRequest.setOrder(order);
                newRefundRequest.setReason(refundRequestDTO.getReason());
                newRefundRequest.setRefundMethod(refundRequestDTO.getRefundMethod());
                newRefundRequest.setStatus(RefundRequestStatusEnum.PENDING);

                newRefundRequest.setRefundShippingFee(refundShippingFee);

                List<RefundItem> newRefundItems = refundRequestDTO.getRefundItems().stream()
                                .map((refundItem) -> {
                                        RefundItem newRefundItem = new RefundItem();
                                        newRefundItem.setProductDetail(productDetailMaps.get(
                                                        refundItem.getOrderItemId()));
                                        newRefundItem.setQuantity(refundItem.getQuantity());
                                        newRefundItem.setRefundAmount(BigDecimal.valueOf(
                                                        orderItemMaps.get(
                                                                        refundItem.getOrderItemId())
                                                                        .getFinalPrice()));
                                        newRefundItem.setRefundRequest(newRefundRequest);
                                        return newRefundItem;
                                })
                                .toList();

                newRefundRequest.setRefundItems(newRefundItems);

                // throw new RuntimeException("Test");
                refundRequestRepository.save(newRefundRequest);

                return refundMapper.toResponseDTO(newRefundRequest);

        }

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

                // order.setStatus(OrderStatusEnum.RETURNED);
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

        @Transactional
        public Page<RefundSummaryDTO> getByCustomerId(Integer customerId, Pageable pageable) {
                return refundRequestRepository.findByOrder_Customer_UserId(customerId, pageable)
                                .map(refundMapper::toSummaryDTO);
        }

        @Transactional
        public Page<RefundSummaryDTO> getAll(Pageable pageable) {
                return refundRequestRepository.findAll(pageable)
                                .map(refundMapper::toSummaryDTO);
        }

        @Transactional
        public RefundResponseDTO getById(Long refundRequestId) {
                RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Refund request not found with ID: " + refundRequestId));
                return refundMapper.toResponseDTO(refundRequest);
        }

        @Transactional
        public RefundResponseDTO updateStatus(Long refundRequestId, RefundRequestStatusEnum status) {
                RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Refund request not found with ID: " + refundRequestId));

                if (refundRequest.getStatus() != RefundRequestStatusEnum.PENDING) {
                        throw new ConflictException(
                                        "Only refund requests with status PENDING can be updated.");
                }
                refundRequest.setStatus(status);
                refundRequestRepository.save(refundRequest);
                return refundMapper.toResponseDTO(refundRequest);
        }

        @Transactional
        public RefundResponseDTO updateRefundMethod(Long refundRequestId, RefundMethodEnum refundMethod) {
                RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Refund request not found with ID: " + refundRequestId));

                if (refundRequest.getStatus() != RefundRequestStatusEnum.PENDING) {
                        throw new ConflictException(
                                        "Only refund requests with status PENDING can be updated.");
                }
                refundRequest.setRefundMethod(refundMethod);
                refundRequestRepository.save(refundRequest);
                return refundMapper.toResponseDTO(refundRequest);
        }

        @Transactional
        public RefundResponseDTO processRefundPayment(Long refundRequestId,
                        CreateRefundPaymentDTO createRefundPaymentDTO) {
                RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Refund request not found with ID: " + refundRequestId));

                if (refundRequest.getStatus() != RefundRequestStatusEnum.APPROVED) {
                        throw new ConflictException(
                                        "Only refund requests with status APPROVED can be processed for payment.");

                }

                RefundPayment refundPayment = new RefundPayment();
                refundPayment.setGatewayRefundId(createRefundPaymentDTO.getGatewayRefundId());
                refundPayment.setImageRefund(createRefundPaymentDTO.getImageRefund());
                refundPayment.setNote(createRefundPaymentDTO.getNote());

                refundRequest.setRefundPayment(refundPayment);
                refundRequest.setStatus(RefundRequestStatusEnum.COMPLETED);

                refundRequestRepository.save(refundRequest);

                return refundMapper.toResponseDTO(refundRequest);
        }

        @Transactional
        public RefundResponseDTO approveRefundRequest(Long refundRequestId) {
                return updateStatus(refundRequestId, RefundRequestStatusEnum.APPROVED);
        }

        @Transactional
        public RefundResponseDTO rejectRefundRequest(Long refundRequestId) {
                return updateStatus(refundRequestId, RefundRequestStatusEnum.REJECTED);
        }

        @Transactional
        public RefundResponseDTO completeRefundRequest(Long refundRequestId, AddRefundPaymentDTO addRefundPaymentDTO) {
                RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Refund request not found with ID: " + refundRequestId));

                if (refundRequest.getStatus() != RefundRequestStatusEnum.APPROVED) {
                        throw new ConflictException(
                                        "Only refund requests with status APPROVED can be completed.");
                }

                RefundPayment refundPayment = new RefundPayment();
                refundPayment.setGatewayRefundId(addRefundPaymentDTO.getGatewayRefundId());
                refundPayment.setImageRefund(addRefundPaymentDTO.getImageRefund());
                refundPayment.setNote(addRefundPaymentDTO.getNote());
                refundRequest.setRefundPayment(refundPayment);

                refundRequest.setStatus(RefundRequestStatusEnum.COMPLETED);

                refundRequestRepository.save(refundRequest);

                return refundMapper.toResponseDTO(refundRequest);
        }

        @Transactional
        public RefundResponseDTO cancelRefundRequest(Integer customerId, Long refundRequestId) {

                RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Refund request not found with ID: " + refundRequestId));

                if (!refundRequest.getOrder().getCustomer().getUserId().equals(customerId)) {
                        throw new ConflictException(
                                        "Customer does not own the refund request with ID: " + refundRequestId);
                }
                if (refundRequest.getStatus() != RefundRequestStatusEnum.PENDING) {
                        throw new ConflictException(
                                        "Only refund requests with status PENDING can be cancelled.");
                }
                refundRequest.setStatus(RefundRequestStatusEnum.CANCEL);
                refundRequestRepository.save(refundRequest);
                return refundMapper.toResponseDTO(refundRequest);

        }

        @Transactional
        public RefundResponseDTO updateRefundRequest(
                        Integer customerId,
                        Long refundRequestId,
                        UpdateRefundRequest updateRefundRequest) {

                RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Refund request not found with ID: " + refundRequestId));

                if (!refundRequest.getOrder().getCustomer().getUserId().equals(customerId)) {
                        throw new ConflictException(
                                        "Customer does not own the refund request with ID: " + refundRequestId);
                }
                if (refundRequest.getStatus() != RefundRequestStatusEnum.PENDING) {
                        throw new ConflictException(
                                        "Only refund requests with status PENDING can be updated.");
                }

                refundRequest.setRefundMethod(updateRefundRequest.getRefundMethod());
                refundRequest.setReason(updateRefundRequest.getReason());

                refundRequestRepository.save(refundRequest);

                return refundMapper.toResponseDTO(refundRequest);
        }

}