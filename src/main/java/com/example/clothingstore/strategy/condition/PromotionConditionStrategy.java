package com.example.clothingstore.strategy.condition;

import java.util.Map;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;

public interface PromotionConditionStrategy {

    // Kiểm tra xem điều kiện khuyến mãi có áp dụng được cho đơn hàng hay không
    boolean isSatisfied(OrderPreviewDTO orderPreviewDTO, Map<String, Object> value);

    // Lấy loại điều kiện khuyến mãi
    PromotionConditionTypeEnum getType();

}