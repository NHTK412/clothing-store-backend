package com.example.clothingstore.dto.refund;

import java.math.BigDecimal;
import java.util.List;

import com.example.clothingstore.enums.RefundMethodEnum;
import com.example.clothingstore.enums.RefundRequestStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefundResponseDTO {

    private Long refundRequestId;
    private Integer orderId;
    private String reason;
    private RefundMethodEnum refundMethod;
    private RefundRequestStatusEnum status;

    private List<RefundItemResponseDTO> refundItems;

    private BigDecimal refundShippingFee;
    private BigDecimal totalRefundAmount;

    private String gatewayRefundId;
    private String imageRefund;
    private String note;

}
