package com.example.clothingstore.strategy;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionTypeEnum;

@Component
public class DiscountStrategy implements PromotionStrategy {

    @Override
    public boolean isApplicable(OrderPreviewDTO orderPreviewDTO) {
        throw new UnsupportedOperationException("Unimplemented method 'apply'");

    }

    @Override
    public void apply(OrderPreviewDTO orderPreviewDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'apply'");
    }

    @Override
    public PromotionTypeEnum getType() {
        return PromotionTypeEnum.DISCOUNT;
    }

}
