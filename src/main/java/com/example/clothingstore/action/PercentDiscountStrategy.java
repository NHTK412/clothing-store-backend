package com.example.clothingstore.action;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;

@Component
public class PercentDiscountStrategy implements PromotionActionStrategy {

    @Override
    public void execute(OrderPreviewDTO orderPreviewDTO, Map<String, Object> value) {
        // Double discountPercentage = (Double) value.get("discountPercentage");

        Number number = (Number) value.get("discountPercentage");
        Double discountPercentage = number.doubleValue();

        if (discountPercentage == null || discountPercentage <= 0) {
            return; // Không có phần trăm giảm giá hợp lệ được cung cấp
        }
        Double discountAmount = orderPreviewDTO.getTotalAmount() * (discountPercentage / 100);
        orderPreviewDTO.setDiscountAmount(orderPreviewDTO.getDiscountAmount() + discountAmount);
        orderPreviewDTO.setFinalAmount(orderPreviewDTO.getFinalAmount() - discountAmount);
        return;
    }

    @Override
    public PromotionActionTypeEnum getType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getType'");
        return PromotionActionTypeEnum.PERCENT_DISCOUNT;
    }

}
