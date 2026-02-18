package com.example.clothingstore.factory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.PromotionTypeEnum;
import com.example.clothingstore.strategy.PromotionStrategy;

@Component
public class PromotionStrategyFactory {

    private final Map<PromotionTypeEnum, PromotionStrategy> strategyMap;

    public PromotionStrategyFactory(List<PromotionStrategy> strategies) {
        this.strategyMap = strategies
                .stream()
                .collect(
                        Collectors.toMap(
                                PromotionStrategy::getType,
                                strategy -> strategy));

    }

    public PromotionStrategy getPromotionStrategy(PromotionTypeEnum type) {
        return strategyMap.get(type);
    }
}
