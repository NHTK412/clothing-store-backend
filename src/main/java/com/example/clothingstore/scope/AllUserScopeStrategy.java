package com.example.clothingstore.scope;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;

@Component
public class AllUserScopeStrategy implements IPromotionScopeStrategy {

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
