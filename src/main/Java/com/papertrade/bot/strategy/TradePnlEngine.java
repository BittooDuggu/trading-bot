package com.papertrade.bot.strategy;

import java.math.BigDecimal;

/** Single-source P/L calculation using exact decimal arithmetic. */
public final class TradePnlEngine {
    private TradePnlEngine() {}

    public static BigDecimal priceMovePoints(Direction direction, BigDecimal entry, BigDecimal exit) {
        if (direction == null || entry == null || exit == null) {
            throw new IllegalArgumentException("direction, entry and exit are required");
        }
        return direction == Direction.BUY ? exit.subtract(entry) : entry.subtract(exit);
    }

    public static BigDecimal pnlValue(Direction direction, BigDecimal entry, BigDecimal exit, BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        return priceMovePoints(direction, entry, exit).multiply(quantity);
    }
}
