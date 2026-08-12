package com.papertrade.bot.strategy;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TradePnlEngineTest {
    @Test
    void sellStopLoss001IsMinus25() {
        assertEquals("-2.5", TradePnlEngine
                .pnlValue(Direction.SELL, bd("62894"), bd("63144"), bd("0.01"))
                .stripTrailingZeros().toPlainString());
    }

    @Test
    void sellStopLoss004IsMinus10() {
        assertEquals("-10", TradePnlEngine
                .pnlValue(Direction.SELL, bd("63081"), bd("63331"), bd("0.04"))
                .stripTrailingZeros().toPlainString());
    }

    @Test
    void buyTarget007IsPlus35() {
        assertEquals("35", TradePnlEngine
                .pnlValue(Direction.BUY, bd("63272"), bd("63772"), bd("0.07"))
                .stripTrailingZeros().toPlainString());
    }

    @Test
    void rawMoveAndPnlAreDifferentUnits() {
        assertEquals("-250", TradePnlEngine
                .priceMovePoints(Direction.SELL, bd("62894"), bd("63144"))
                .stripTrailingZeros().toPlainString());
        assertEquals("-2.5", TradePnlEngine
                .pnlValue(Direction.SELL, bd("62894"), bd("63144"), bd("0.01"))
                .stripTrailingZeros().toPlainString());
    }

    private static BigDecimal bd(String s) { return new BigDecimal(s); }
}
