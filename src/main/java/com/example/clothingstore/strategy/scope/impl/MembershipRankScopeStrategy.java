package com.example.clothingstore.strategy.scope.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Promotion;
import com.example.clothingstore.strategy.scope.PromotionScopeStrategy;

@Component
public class MembershipRankScopeStrategy implements PromotionScopeStrategy {

    @Override
    public boolean isScopeSatisfied(Customer customerContext, Promotion promotionContext) {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'isScopeSatisfied'");

        List<Integer> requiredMembershipRanks = promotionContext.getPromotionMemberTiers().stream()
                .map(promotionMemberTier -> promotionMemberTier.getMembershipTier().getTiedId())
                .toList();

        return requiredMembershipRanks.contains(customerContext.getMembershipTier().getTiedId());
    }

    @Override
    public PromotionScopeTypeEnum getPromotionScopeType() {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'getPromotionScopeType'");

        return PromotionScopeTypeEnum.MEMBER_RANK;
    }

}
