package com.example.clothingstore.scope;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;

public interface IPromotionScopeStrategy {

    boolean isScopeSatisfied(Customer customerContext, Promotion promotionContext);

    PromotionScopeTypeEnum getPromotionScopeType();




}
