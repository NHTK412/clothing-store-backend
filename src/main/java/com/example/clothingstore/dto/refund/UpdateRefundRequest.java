package com.example.clothingstore.dto.refund;

import com.example.clothingstore.enums.RefundMethodEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateRefundRequest {

    RefundMethodEnum refundMethod;

    String reason;

}
