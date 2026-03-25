package com.example.clothingstore.dto.orderdetail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetailPreviewDTO {
    private Integer productDetailId;

    private String productName;

    private String productImage;

    private String color;

    private String size;

    private Integer quantity;

    private Double price;

    private Double discountAmount; // Số tiền giảm giá cho sản phẩm này

    private Double finalPrice;

    private Boolean isFree; // Sản phẩm này có phải là quà tặng miễn phí hay không

}
