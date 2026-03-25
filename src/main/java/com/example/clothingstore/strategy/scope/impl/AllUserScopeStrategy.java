package com.example.clothingstore.strategy.scope.impl;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.strategy.scope.PromotionScopeStrategy;

@Component
public class AllUserScopeStrategy implements PromotionScopeStrategy {

    @Override
    public boolean isScopeSatisfied(Customer customerContext, Promotion promotionContext) {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'isScopeSatisfied'");

        return true;
    }

    @Override
    public PromotionScopeTypeEnum getPromotionScopeType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getPromotionScopeType'");

        return PromotionScopeTypeEnum.ALL_USER;
    }

}
