package com.example.clothingstore.dto.orderdetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderDetailResponseDTO {

    private Integer orderDetailId;

    private String productName;

    private String productImage;

    private String color;

    private String size;

    private Integer quantity;

    private Double price;

    private Double discount;

    private Double finalPrice;

    private Integer productId;

    private Boolean isReview;

    // private Boolean isRefunded;

    private Integer refundQuantity;

}
