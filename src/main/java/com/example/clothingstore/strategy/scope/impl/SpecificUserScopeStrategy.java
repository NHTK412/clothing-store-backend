package com.example.clothingstore.strategy.scope.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.strategy.scope.PromotionScopeStrategy;

@Component
public class SpecificUserScopeStrategy implements PromotionScopeStrategy {

    @Override
    public boolean isScopeSatisfied(Customer customerContext, Promotion promotionContext) {

        List<Integer> targetUserIds = promotionContext.getPromotionTargetUsers().stream()
                .map(promotionTargetUser -> promotionTargetUser.getCustomer().getUserId())
                .collect(Collectors.toList());

        return targetUserIds.contains(customerContext.getUserId());
    }

    @Override
    public PromotionScopeTypeEnum getPromotionScopeType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'getPromotionScopeType'");

        return PromotionScopeTypeEnum.SPECIFIC_USER;
    }

}
