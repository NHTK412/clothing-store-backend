package com.example.clothingstore.dto.order;

import java.time.LocalDateTime;
import java.util.List;

import com.example.clothingstore.dto.orderdetail.OrderDetailRequestDTO;
import com.example.clothingstore.enums.PaymentMethodEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    // private Double shippingFee;

    // private LocalDateTime deliveryDate; // Ngày giao hàng dự kiến

    @NotNull(message = "Address shipping ID is required")
    private Integer addressShippingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethodEnum paymentMethod;

    // private String vnpayCode;

    // private Integer customerId;

    @Valid
    @NotEmpty(message = "Order details cannot be empty")
    private List<OrderDetailRequestDTO> orderDetailRequestDTOs;

    // private Integer promotionDiscountId; // Mã khuyến mãi giảm giá

    // danh sách mã quà tặng
    // private List<Integer> promotionGiftIds;
}
