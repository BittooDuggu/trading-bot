package com.papertrade.bot.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class QuantityEngineTest {

    private StrategyConfig config() {
        return new StrategyConfig(
                new BigDecimal("0.01"),
                new BigDecimal("500"),
                new BigDecimal("250"),
                new BigDecimal("400")
        );
    }

    @Test
    void firstStopLossIs002() {
        var t = QuantityEngine.afterExit(
                config(), new BigDecimal("0.01"), Direction.BUY, ExitType.STOP_LOSS
        );
        assertEquals("2.5", t.actualLoss().stripTrailingZeros().toPlainString());
        assertEquals("0.01", t.recoveryQuantity().toPlainString());
        assertEquals("0.02", t.nextQuantity().toPlainString());
        assertEquals(Direction.SELL, t.nextDirection());
    }

    @Test
    void secondAndThirdStopLossesFollowExactChain() {
        var c = config();

        var t1 = QuantityEngine.afterExit(c, bd("0.01"), Direction.BUY, ExitType.STOP_LOSS);
        var t2 = QuantityEngine.afterExit(c, t1.nextQuantity(), t1.nextDirection(), ExitType.STOP_LOSS);
        var t3 = QuantityEngine.afterExit(c, t2.nextQuantity(), t2.nextDirection(), ExitType.STOP_LOSS);

        assertEquals("0.02", t1.nextQuantity().toPlainString());
        assertEquals("0.04", t2.nextQuantity().toPlainString());
        assertEquals("0.07", t3.nextQuantity().toPlainString());
    }

    @Test
    void targetAlwaysResetsToStartingQuantity() {
        var t = QuantityEngine.afterExit(
                config(), bd("16.27"), Direction.SELL, ExitType.TARGET
        );
        assertEquals("0.01", t.nextQuantity().toPlainString());
        assertEquals(Direction.BUY, t.nextDirection());
    }

    @Test
    void noFloatingPointDriftAfterManyStopLosses() {
        var c = config();
        BigDecimal qty = c.startingQuantity();
        Direction dir = Direction.BUY;

        for (int i = 0; i < 100; i++) {
            var t = QuantityEngine.afterExit(c, qty, dir, ExitType.STOP_LOSS);
            assertEquals(c.quantityScale(), t.nextQuantity().scale());
            assertTrue(t.nextQuantity().signum() > 0);
            qty = t.nextQuantity();
            dir = t.nextDirection();
        }
    }

    private static BigDecimal bd(String s) {
        return new BigDecimal(s);
    }
}
