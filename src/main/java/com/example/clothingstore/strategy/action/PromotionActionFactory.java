package com.example.clothingstore.strategy.action;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionActionTypeEnum;

@Component
public class PromotionActionFactory {

    Map<PromotionActionTypeEnum, PromotionActionStrategy> actionStrategyMap;

    public PromotionActionFactory(List<PromotionActionStrategy> strategies) {
        this.actionStrategyMap = strategies
                .stream()
                .collect(
                        Collectors.toMap(
                                strategie -> strategie.getType(),
                                strategy -> strategy));
    }

    public PromotionActionStrategy getPromotionActionStrategy(PromotionActionTypeEnum type) {
        return actionStrategyMap.get(type);
    }

}
