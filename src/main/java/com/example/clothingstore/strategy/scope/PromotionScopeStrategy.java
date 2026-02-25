package com.example.clothingstore.strategy.scope;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;

public interface PromotionScopeStrategy {

    boolean isScopeSatisfied(Customer customerContext, Promotion promotionContext);

    PromotionScopeTypeEnum getPromotionScopeType();




}
