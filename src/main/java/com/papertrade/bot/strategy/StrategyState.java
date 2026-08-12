package com.papertrade.bot.strategy;

import java.math.BigDecimal;

public record StrategyState(
        Direction direction,
        BigDecimal quantity
) {}
