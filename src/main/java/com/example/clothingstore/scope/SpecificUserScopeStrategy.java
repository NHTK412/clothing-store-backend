package com.example.clothingstore.scope;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;

@Component
public class SpecificUserScopeStrategy implements IPromotionScopeStrategy {

    @Override
    public boolean isScopeSatisfied(Customer customerContext, Promotion promotionContext) {

        List<Integer> targetUserIds = promotionContext.getPromotionTargetUsers().stream()
                .map(promotionTargetUser -> promotionTargetUser.getCustomer().getCustomerId())
                .collect(Collectors.toList());

        return targetUserIds.contains(customerContext.getCustomerId());
    }

    @Override
    public PromotionScopeTypeEnum getPromotionScopeType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'getPromotionScopeType'");

        return PromotionScopeTypeEnum.SPECIFIC_USER;
    }

}
