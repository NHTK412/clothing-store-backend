package com.example.clothingstore.dto.refund;

import java.math.BigDecimal;

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
public class RefundSummaryDTO {

    private Long refundRequestId;
    private Integer orderId;
    private String reason;
    private RefundMethodEnum refundMethod;
    private BigDecimal refundShippingFee;
    private BigDecimal totalRefundAmount;
    private RefundRequestStatusEnum status;

}
