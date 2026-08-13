package com.papertrade.bot.backtest;

import com.papertrade.bot.strategy.Direction;
import java.math.BigDecimal;
import java.util.List;

public record BacktestRequest(
        BigDecimal startingQuantity,
        BigDecimal targetPoints,
        BigDecimal stopLossPoints,
        BigDecimal recoveryPoints,
        Direction firstDirection,
        BothHitRule bothHitRule,
        List<Candle> candles
) {
    public enum BothHitRule { SL_FIRST, TARGET_FIRST }

    public record Candle(
            long time,
            BigDecimal open,
            BigDecimal high,
            BigDecimal low,
            BigDecimal close,
            BigDecimal volume
    ) {}
}
