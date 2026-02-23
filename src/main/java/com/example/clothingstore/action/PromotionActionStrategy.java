package com.example.clothingstore.action;

import java.util.Map;

import com.example.clothingstore.dto.order.OrderPreviewDTO;
import com.example.clothingstore.enums.PromotionActionTypeEnum;
import com.example.clothingstore.model.Promotion;

public interface PromotionActionStrategy {

    PromotionActionTypeEnum getType();

    // void execute(OrderPreviewDTO orderPreviewDTO, Map<String, Object> value);
    void execute(OrderPreviewDTO orderPreviewDTO, Promotion promotionContext, Integer actionIndex);

}