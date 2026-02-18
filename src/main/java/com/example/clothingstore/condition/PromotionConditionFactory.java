package com.example.clothingstore.condition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionConditionTypeEnum;

@Component
public class PromotionConditionFactory {

    Map<PromotionConditionTypeEnum, PromotionConditionStrategy> conditionStrategyMap;

    public PromotionConditionFactory(List<PromotionConditionStrategy> strategies) {
        this.conditionStrategyMap = strategies
                .stream()
                .collect(
                        Collectors.toMap(
                                strategie -> strategie.getType(),
                                strategy -> strategy));
    }

    public PromotionConditionStrategy getPromotionConditionStrategy(PromotionConditionTypeEnum type) {
        return conditionStrategyMap.get(type);
    }

}
