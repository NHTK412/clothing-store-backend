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

    @NotNull(message = "Address shipping ID is required")
    private Integer addressShippingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethodEnum paymentMethod;

    @Valid
    @NotEmpty(message = "Order details cannot be empty")
    private List<OrderDetailRequestDTO> orderDetailRequestDTOs;

    private List<Integer> promotionApplyIds;

    private Double totalAmount;

    private Double discount;

    private Double shippingFee;

    private Double discountShippingFee; // Số tiền giảm giá cho phí vận chuyển

    private Double finalAmount;

}
