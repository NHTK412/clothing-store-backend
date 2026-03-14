package com.example.clothingstore.dto.refund;

import java.util.List;

import com.example.clothingstore.enums.RefundMethodEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefundRequestDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class RefundItemDTO {
        private Integer orderItemId;
        private Integer quantity;
    }

    private Integer orderId;
    private String reason;

    private RefundMethodEnum refundMethod;
    private List<RefundItemDTO> refundItems;

}
