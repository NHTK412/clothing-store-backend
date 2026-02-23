package com.example.clothingstore.action;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.model.Promotion;

@Component
public class PercentDiscountStrategy implements PromotionActionStrategy {

    @Override
    public void execute(OrderPreviewDTO orderPreviewDTO, Promotion promotionContext, Integer actionIndex) {
        // Double discountPercentage = (Double) value.get("discountPercentage");

        Map<String, Object> value = promotionContext.getPromotionActions().get(actionIndex).getValue();

        Number number = (Number) value.get("discountPercentage");
        Double discountPercentage = number.doubleValue();

        if (discountPercentage == null || discountPercentage <= 0) {
            return; // Không có phần trăm giảm giá hợp lệ được cung cấp
        }
        Double discountAmount = 0.0;
        // Double currentFinalAmount = orderPreviewDTO.getFinalAmount();

        // if (promotionContext.getStackable()) {

        // Double remainingAmount = orderPreviewDTO.getTotalAmount() -
        // orderPreviewDTO.getDiscountAmount();

        // Double discount = remainingAmount * (discountPercentage / 100);

        // // currentFinalAmount = currentFinalAmount - discount;

        // discountAmount = orderPreviewDTO.getDiscountAmount() + discount;

        // } else {
        // discountAmount = orderPreviewDTO.getTotalAmount() * (discountPercentage /
        // 100);

        // // currentFinalAmount = orderPreviewDTO.getFinalAmount() +
        // // orderPreviewDTO.getDiscountAmount()
        // // - discountAmount;

        // if (discountAmount < orderPreviewDTO.getDiscountAmount()) {
        // // Nếu giảm giá mới nhỏ hơn giảm giá hiện tại, giữ nguyên giảm giá hiện tại
        // discountAmount = orderPreviewDTO.getDiscountAmount();

        // // currentFinalAmount = currentFinalAmount - discountAmount;

        // }

        // }

        // if (discountAmount >= orderPreviewDTO.getTotalAmount()) {
        // discountAmount = orderPreviewDTO.getTotalAmount();
        // }

        Double remainingAmount = orderPreviewDTO.getTotalAmount() - orderPreviewDTO.getDiscountAmount();

        Double discount = remainingAmount * (discountPercentage / 100);

        // currentFinalAmount = currentFinalAmount - discount;

        discountAmount = orderPreviewDTO.getDiscountAmount() + discount;

        Double currentFinalAmount = orderPreviewDTO.getTotalAmount() - discountAmount + orderPreviewDTO.getShippingFee()
                - orderPreviewDTO.getDiscountShippingFee();

        orderPreviewDTO.setDiscountAmount(discountAmount);
        orderPreviewDTO.setFinalAmount(currentFinalAmount);
        // orderPreviewDTO.setFinalAmount(orderPreviewDTO.getFinalAmount() -
        // discountAmount);
        return;
    }

    @Override
    public PromotionActionTypeEnum getType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getType'");
        return PromotionActionTypeEnum.PERCENT_DISCOUNT;
    }

}

// Chưa tính trường hợp nếu cho nạp chồng
