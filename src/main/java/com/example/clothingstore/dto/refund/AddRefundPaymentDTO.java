package com.example.clothingstore.dto.refund;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddRefundPaymentDTO {
    private String gatewayRefundId;
    private String imageRefund;
    private String note;

}
