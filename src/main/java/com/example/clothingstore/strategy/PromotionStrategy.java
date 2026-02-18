package com.example.clothingstore.strategy;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionTypeEnum;

public interface PromotionStrategy {


    PromotionTypeEnum getType();

    boolean isApplicable(OrderPreviewDTO orderPreviewDTO); // Kiểm tra xem chiến lược khuyến mãi có áp dụng được cho tổng số tiền hay
                                              // không

    void apply(OrderPreviewDTO orderPreviewDTO);

}