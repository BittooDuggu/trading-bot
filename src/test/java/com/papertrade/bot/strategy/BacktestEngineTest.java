package com.papertrade.bot.strategy;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BacktestEngineTest {
    private static BigDecimal bd(String s) { return new BigDecimal(s); }
    private static BacktestEngine.Candle c(long t, String o, String h, String l) {
        return new BacktestEngine.Candle(t, bd(o), bd(h), bd(l), bd(o), BigDecimal.ZERO);
    }

    @Test
    void repeatedStopsProduceExactChain() {
        var cfg = new BacktestEngine.Config(bd("0.01"), bd("500"), bd("250"), bd("400"), Direction.BUY, BacktestEngine.BothHitRule.TARGET_FIRST);
        var candles = List.of(
                c(1,"1000","1000","750"),
                c(2,"1250","1500","1250"),
                c(3,"1000","1000","750")
        );
        var out = BacktestEngine.run(candles, cfg);
        assertEquals(3, out.trades().size());
        assertEquals("0.01", out.trades().get(0).quantity().toPlainString());
        assertEquals("0.02", out.trades().get(1).quantity().toPlainString());
        assertEquals("0.04", out.trades().get(2).quantity().toPlainString());
        assertEquals("0.07", out.trades().get(2).nextQuantity().toPlainString());
        assertEquals("-2.5", out.trades().get(0).pnl().stripTrailingZeros().toPlainString());
        assertEquals("-5", out.trades().get(1).pnl().stripTrailingZeros().toPlainString());
        assertEquals("-10", out.trades().get(2).pnl().stripTrailingZeros().toPlainString());
    }
}
