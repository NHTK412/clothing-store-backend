package com.example.clothingstore.condition;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionConditionTypeEnum;

@Component
public class MinOrderAmountStrategy implements PromotionConditionStrategy {

    @Override
    public boolean isSatisfied(OrderPreviewDTO orderPreviewDTO, Map<String, Object> value) {

        // Double minOrderAmount = (Double) value.get("minOrderAmount");
        Number number = (Number) value.get("minOrderAmount");
        Double minOrderAmount = number.doubleValue();

        if (minOrderAmount == null || minOrderAmount <= 0) {
            return false; // Không có số tiền tối thiểu hợp lệ được cung cấp
        }

        return orderPreviewDTO.getTotalAmount() >= minOrderAmount;

    }

    @Override
    public PromotionConditionTypeEnum getType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getType'");
        return PromotionConditionTypeEnum.MIN_ORDER_AMOUNT;
    }

}
