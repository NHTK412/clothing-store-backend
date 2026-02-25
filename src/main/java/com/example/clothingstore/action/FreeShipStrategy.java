package com.example.clothingstore.action;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.model.Promotion;

@Component
public class FreeShipStrategy implements PromotionActionStrategy {

    @Override
    public PromotionActionTypeEnum getType() {
        return PromotionActionTypeEnum.FREE_SHIP;
    }

    @Override
    public void execute(OrderPreviewDTO orderPreviewDTO, Promotion promotionContext, Integer actionIndex) {

        Map<String, Object> value = promotionContext.getPromotionActions().get(actionIndex).getValue();

        Number discountShipNumber = (Number) value.get("discountPercentageShip");

        if (discountShipNumber == null) {
            return;
        }

        Double discountShip = discountShipNumber.doubleValue();

        if (discountShip <= 0) {
            return;
        }

        Double shippingFee = orderPreviewDTO.getShippingFee() - orderPreviewDTO.getDiscountShippingFee();

        Double discountAmount = shippingFee * (discountShip / 100);

        orderPreviewDTO.setDiscountShippingFee(discountAmount);

        orderPreviewDTO.setFinalAmount(orderPreviewDTO.getFinalAmount() - discountAmount);

        return;

    }

}
