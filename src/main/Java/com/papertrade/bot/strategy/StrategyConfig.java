package com.papertrade.bot.strategy;

import java.math.BigDecimal;

public record StrategyConfig(
        BigDecimal startingQuantity,
        BigDecimal targetPoints,
        BigDecimal stopLossPoints,
        BigDecimal recoveryPoints
) {
    public StrategyConfig {
        if (startingQuantity == null || startingQuantity.signum() <= 0) {
            throw new IllegalArgumentException("startingQuantity must be > 0");
        }
        if (targetPoints == null || targetPoints.signum() <= 0) {
            throw new IllegalArgumentException("targetPoints must be > 0");
        }
        if (stopLossPoints == null || stopLossPoints.signum() <= 0) {
            throw new IllegalArgumentException("stopLossPoints must be > 0");
        }
        if (recoveryPoints == null || recoveryPoints.signum() <= 0) {
            throw new IllegalArgumentException("recoveryPoints must be > 0");
        }
    }

    /** Quantity precision comes from the starting quantity, e.g. 0.01 => 2 decimals. */
    public int quantityScale() {
        return Math.max(0, startingQuantity.stripTrailingZeros().scale());
    }
}
