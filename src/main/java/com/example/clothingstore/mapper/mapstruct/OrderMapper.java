package com.example.clothingstore.mapper.mapstruct;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.annotation.After;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.dto.order.OrderRequestDTO;
import com.example.clothingstore.dto.order.OrderResponseDTO;
import com.example.clothingstore.dto.order.OrderSummaryDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailPreviewDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailRequestDTO;
import com.example.clothingstore.dto.orderdetail.OrderDetailResponseDTO;
import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;
import com.example.clothingstore.model.Address;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Order;
import com.example.clothingstore.model.OrderDetail;
import com.example.clothingstore.model.ProductDetail;
import com.example.clothingstore.model.RefundRequest;

@Mapper(componentModel = "spring")
public interface OrderMapper {

        @Mapping(source = "order.totalAmount", target = "totalAmount")
        OrderResponseDTO toResponseDTO(Order order);

        @Mapping(source = "orderDetail.productDetail.productColor.product.productId", target = "productId")
        @Mapping(target = "orderDetailId", source = "orderDetail.detailId")
        OrderDetailResponseDTO toDetailResponseDTO(OrderDetail orderDetail);

        @AfterMapping
        default void setRefundQuantity(OrderDetail orderDetail,
                        @MappingTarget OrderDetailResponseDTO orderDetailResponseDTO) {
                // List<RefundRequest> refundRequests = refundRequestRepository
                // .findByOrder_OrderId(orderDetail.getOrder().getOrderId());

                List<RefundRequest> refundRequests = orderDetail.getOrder().getRefundRequests();

                if (refundRequests == null || refundRequests.isEmpty()) {
                        orderDetailResponseDTO.setRefundQuantity(0);
                        return;
                }

                Integer refundQuantity = refundRequests.stream()
                                .flatMap(rr -> rr.getRefundItems().stream())
                                .filter(ri -> ri.getProductDetail().getDetailId()
                                                .equals(orderDetail.getProductDetail().getDetailId()))
                                .mapToInt(ri -> ri.getQuantity())
                                .sum();
                orderDetailResponseDTO.setRefundQuantity(refundQuantity);

        }

        OrderSummaryDTO toSummaryDTO(Order order);

        @AfterMapping
        default void setOrderFist(Order order, @MappingTarget OrderSummaryDTO orderSummaryDTO) {
                orderSummaryDTO.setOrderFirstName(order.getOrderDetails().getFirst().getProductName());
                orderSummaryDTO.setOrderFirstImage(order.getOrderDetails().getFirst().getProductImage());
        }

        @AfterMapping
        default void setOrderQuantity(Order order, @MappingTarget OrderSummaryDTO orderSummaryDTO) {
                Integer totalQuantity = order.getOrderDetails().stream()
                                .mapToInt(OrderDetail::getQuantity)
                                .sum();
                orderSummaryDTO.setOrderQuantity(totalQuantity);
        }

        @AfterMapping
        default void setIsReview(Order order, @MappingTarget OrderSummaryDTO orderSummaryDTO) {
                Boolean isReview = order.getOrderDetails().stream()
                                .allMatch((orderDetail) -> orderDetail.getIsReview() == true);

                orderSummaryDTO.setIsReview(isReview);
        }

        default OrderPreviewDTO toPreviewDTO(Set<OrderDetailPreviewDTO> orderDetailPreviews) {
                Double totalAmount = orderDetailPreviews.stream()
                                .mapToDouble(od -> od.getFinalPrice() * od.getQuantity())
                                .sum();

                Double shippingFee = 30000.0;

                Double finalAmount = totalAmount + shippingFee;

                return OrderPreviewDTO.builder()
                                .orderDetails(orderDetailPreviews)
                                .totalAmount(totalAmount)
                                .discountAmount(0.0)
                                .shippingFee(shippingFee)
                                .discountShippingFee(0.0)
                                .finalAmount(finalAmount)
                                .appliedPromotions(new ArrayList<>())
                                .build();
        }

        default Set<OrderDetailPreviewDTO> toSetDetailPreviewDTO(
                        List<ProductDetail> productDetails,
                        Map<Integer, OrderDetailRequestDTO> orderDetailRequestMap) {
                return productDetails.stream()
                                .map(pd -> toOrderDetailPreviewDTO(pd,
                                                orderDetailRequestMap.get(pd.getDetailId())))
                                .collect(Collectors.toSet());
        }

        default OrderDetailPreviewDTO toOrderDetailPreviewDTO(
                        ProductDetail productDetail,
                        OrderDetailRequestDTO orderDetailRequest) {
                Double discountAmount = productDetail.getProductColor().getProduct().getDiscount() != null
                                ? productDetail.getProductColor().getProduct().getDiscount()
                                : 0.0;

                Double price = productDetail.getProductColor().getProduct().getUnitPrice();
                Double finalPrice = price - discountAmount;

                return OrderDetailPreviewDTO.builder()
                                .productDetailId(productDetail.getDetailId())
                                .productName(productDetail.getProductColor().getProduct().getProductName())
                                .productImage(productDetail.getProductColor().getProduct().getProductImage())
                                .color(productDetail.getProductColor().getColor())
                                .size(productDetail.getSize())
                                .quantity(orderDetailRequest.getQuantity())
                                .price(price)
                                .discountAmount(discountAmount)
                                .finalPrice(finalPrice)
                                .isFree(false)
                                .build();
        }

        @Mapping(target = "orderDetails", ignore = true)
        Order toOrder(
                        OrderPreviewDTO orderPreview,
                        @Context Address shippingAddress,
                        @Context Customer customer,
                        @Context OrderRequestDTO orderRequest,
                        @Context Map<Integer, ProductDetail> productDetailMap);

        @AfterMapping
        default void enrichOrder(
                        @MappingTarget Order order,
                        @Context Address shippingAddress,
                        @Context Customer customer,
                        OrderPreviewDTO orderPreview,
                        @Context OrderRequestDTO orderRequest,
                        @Context Map<Integer, ProductDetail> productDetailMap) {

                // shipping info
                order.setRecipientName(shippingAddress.getRecipientName());
                order.setPhoneNumber(shippingAddress.getPhoneNumber());
                order.setDetailedAddress(shippingAddress.getDetailedAdress());
                order.setWard(shippingAddress.getWard());
                order.setProvince(shippingAddress.getProvince());

                // init
                order.setCustomer(customer);
                order.setStatus(OrderStatusEnum.PLACED);
                order.setPaymentStatus(OrderPaymentStatusEnum.UNPAID);

                // final
                order.setPaymentMethod(orderRequest.getPaymentMethod());
                order.setShippingFee(orderPreview.getShippingFee());
                order.setDiscountAmount(orderPreview.getDiscountAmount());
                order.setDiscountShippingFee(orderPreview.getDiscountShippingFee());
                order.setFinalAmount(orderPreview.getFinalAmount());
                order.setTotalAmount(orderPreview.getTotalAmount());

                if (orderPreview.getOrderDetails() == null || orderPreview.getOrderDetails().isEmpty()) {
                        order.setOrderDetails(new ArrayList<>());
                } else {
                        List<OrderDetail> orderDetails = orderPreview.getOrderDetails().stream()
                                        .map(preview -> toOrderDetail(preview, productDetailMap, order))
                                        .toList();
                        order.setOrderDetails(orderDetails);
                }

                order.setIsReview(false);
        }

        // @Mapping(target = "productDetail", ignore = true)
        // @Mapping(target = "order", ignore = true)
        @Mapping(target = "isReview", constant = "false")
        OrderDetail toOrderDetail(OrderDetailPreviewDTO preview,
                        @Context Map<Integer, ProductDetail> productDetailMap,
                        @Context Order order);

        @AfterMapping
        default void setRelation(@MappingTarget OrderDetail orderDetail,
                        OrderDetailPreviewDTO preview,
                        @Context Map<Integer, ProductDetail> productDetailMap,
                        @Context Order order) {
                orderDetail.setProductDetail(
                                productDetailMap.get(preview.getProductDetailId()));
                orderDetail.setOrder(order);
        }

}
