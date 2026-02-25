package com.example.clothingstore.dto.order;

import java.time.LocalDateTime;
import java.util.List;

import com.example.clothingstore.dto.orderdetail.OrderDetailResponseDTO;
import com.example.clothingstore.dto.ordergift.OrderGiftResponseDTO;
import com.example.clothingstore.enums.OrderPaymentStatusEnum;
import com.example.clothingstore.enums.OrderStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private Integer orderId;

    private Double totalAmount;

    private Double discountAmount;

    private Double shippingFee;

    private Double discountShippingFee;

    private Double finalAmount;

    private LocalDateTime deliveryDate;

    private OrderStatusEnum status;

    private String recipientName;

    private String phoneNumber;

    private String detailedAddress;

    private String ward;

    private String province;

    // private String paymentMethod;
    private OrderPaymentStatusEnum paymentStatus;

    private String zaloAppTransId;

    private List<OrderDetailResponseDTO> orderDetails;

    private List<OrderGiftResponseDTO> orderGifts;

    private Boolean isReview;

}
