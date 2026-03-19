package com.example.clothingstore.dto.refund;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRefundPaymentDTO {

    private String gatewayRefundId;
    private String imageRefund;
    private String note;

}
