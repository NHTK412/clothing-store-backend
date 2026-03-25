package com.example.clothingstore.strategy.scope;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;

@Component
public class PromotionScopeFactory {

    Map<PromotionScopeTypeEnum, PromotionScopeStrategy> promotionScopeStrategies;

    public PromotionScopeFactory(List<PromotionScopeStrategy> promotionScopeStrategies) {
        this.promotionScopeStrategies = promotionScopeStrategies
                .stream()
                .collect(Collectors.toMap(
                        (promotionScopeStrategy) -> promotionScopeStrategy.getPromotionScopeType()

                        , (promotionScopeStrategy) -> promotionScopeStrategy));

    }

    public PromotionScopeStrategy getPromotionScopeStrategy(PromotionScopeTypeEnum promotionScopeType) {
        return promotionScopeStrategies.get(promotionScopeType);
    }

}
