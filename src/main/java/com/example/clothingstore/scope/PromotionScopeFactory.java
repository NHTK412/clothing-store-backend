package com.example.clothingstore.scope;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionScopeTypeEnum;

@Component
public class PromotionScopeFactory {

    Map<PromotionScopeTypeEnum, IPromotionScopeStrategy> promotionScopeStrategies;

    public PromotionScopeFactory(List<IPromotionScopeStrategy> promotionScopeStrategies) {
        this.promotionScopeStrategies = promotionScopeStrategies
                .stream()
                .collect(Collectors.toMap(
                        (promotionScopeStrategy) -> promotionScopeStrategy.getPromotionScopeType()

                        , (promotionScopeStrategy) -> promotionScopeStrategy));

    }

    public IPromotionScopeStrategy getPromotionScopeStrategy(PromotionScopeTypeEnum promotionScopeType) {
        return promotionScopeStrategies.get(promotionScopeType);
    }

}
