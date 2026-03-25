package com.example.clothingstore.dto.refund;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefundItemResponseDTO {
    private Integer refundItemId;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private String size;
    private String color;
    private Integer quantity;
    private BigDecimal refundAmount;

}
