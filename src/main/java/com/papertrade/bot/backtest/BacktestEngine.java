package com.papertrade.bot.backtest;

import com.papertrade.bot.strategy.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BacktestEngine {
    private BacktestEngine() {}

    public static Result run(BacktestRequest request) {
        if (request.candles() == null || request.candles().size() < 2) {
            throw new IllegalArgumentException("At least 2 candles are required.");
        }
        StrategyConfig cfg = new StrategyConfig(
                request.startingQuantity(), request.targetPoints(),
                request.stopLossPoints(), request.recoveryPoints());
        Direction direction = request.firstDirection() == null ? Direction.BUY : request.firstDirection();
        List<BacktestRequest.Candle> candles = request.candles().stream()
                .filter(c -> c != null && c.open() != null && c.high() != null && c.low() != null && c.time() > 0)
                .sorted(Comparator.comparingLong(BacktestRequest.Candle::time))
                .toList();

        List<TradeResult> trades = new ArrayList<>();
        BigDecimal quantity = cfg.startingQuantity();
        BigDecimal net = BigDecimal.ZERO;
        BigDecimal peak = BigDecimal.ZERO;
        BigDecimal maxDrawdown = BigDecimal.ZERO;
        int i = 0;

        while (i < candles.size()) {
            BacktestRequest.Candle entryCandle = candles.get(i);
            Direction tradeDirection = direction;
            BigDecimal entry = entryCandle.open();
            BigDecimal target = tradeDirection == Direction.BUY
                    ? entry.add(cfg.targetPoints()) : entry.subtract(cfg.targetPoints());
            BigDecimal sl = tradeDirection == Direction.BUY
                    ? entry.subtract(cfg.stopLossPoints()) : entry.add(cfg.stopLossPoints());

            Exit exit = null;
            int exitIndex = -1;
            for (int j = i; j < candles.size(); j++) {
                exit = findExit(tradeDirection, target, sl, candles.get(j), request.bothHitRule());
                if (exit != null) { exitIndex = j; break; }
            }
            if (exit == null) break;

            BacktestRequest.Candle exitCandle = candles.get(exitIndex);
            BigDecimal points = tradeDirection == Direction.BUY
                    ? exit.price.subtract(entry) : entry.subtract(exit.price);
            BigDecimal pnl = points.multiply(quantity);
            QuantityEngine.Transition transition = QuantityEngine.afterExit(
                    cfg, quantity, tradeDirection, exit.type);

            net = net.add(pnl);
            if (net.compareTo(peak) > 0) peak = net;
            BigDecimal dd = peak.subtract(net);
            if (dd.compareTo(maxDrawdown) > 0) maxDrawdown = dd;

            trades.add(new TradeResult(
                    trades.size() + 1,
                    entryCandle.time(), exitCandle.time(), tradeDirection, quantity,
                    entry, exit.price, target, sl, exit.type.name(), exit.bothHit,
                    points, pnl, transition.actualLoss(),
                    transition.recoveryQuantity(), transition.nextQuantity(),
                    transition.nextDirection().name(), exit.type == ExitType.TARGET ? "TARGET_RESET" : "STOP_RECOVERY"
            ));

            direction = transition.nextDirection();
            quantity = transition.nextQuantity();
            i = exitIndex + 1;
        }

        audit(trades, cfg);
        return new Result(trades, net, maxDrawdown, quantity, direction, candles.size());
    }

    private static Exit findExit(Direction direction, BigDecimal target, BigDecimal sl,
                                 BacktestRequest.Candle candle, BacktestRequest.BothHitRule rule) {
        boolean hitTarget = direction == Direction.BUY
                ? candle.high().compareTo(target) >= 0 : candle.low().compareTo(target) <= 0;
        boolean hitSl = direction == Direction.BUY
                ? candle.low().compareTo(sl) <= 0 : candle.high().compareTo(sl) >= 0;
        if (hitTarget && hitSl) {
            if (rule == BacktestRequest.BothHitRule.TARGET_FIRST)
                return new Exit(ExitType.TARGET, target, true);
            return new Exit(ExitType.STOP_LOSS, sl, true);
        }
        if (hitSl) return new Exit(ExitType.STOP_LOSS, sl, false);
        if (hitTarget) return new Exit(ExitType.TARGET, target, false);
        return null;
    }

    private static void audit(List<TradeResult> trades, StrategyConfig cfg) {
        for (int i = 0; i < trades.size() - 1; i++) {
            TradeResult t = trades.get(i), n = trades.get(i + 1);
            Direction expectedDirection = t.direction.opposite();
            if (n.direction != expectedDirection)
                throw new IllegalStateException("Quantity-chain audit failed: direction after trade #" + t.no);
            BigDecimal expectedQty;
            if ("TARGET".equals(t.reason)) expectedQty = cfg.startingQuantity();
            else expectedQty = QuantityEngine.afterExit(
                    cfg, t.quantity, t.direction, ExitType.STOP_LOSS).nextQuantity();
            if (n.quantity.compareTo(expectedQty) != 0)
                throw new IllegalStateException("Quantity-chain audit failed after trade #" + t.no
                        + ": expected " + expectedQty.toPlainString() + " but got " + n.quantity.toPlainString());
        }
    }

    private record Exit(ExitType type, BigDecimal price, boolean bothHit) {}

    public record Result(
            List<TradeResult> trades,
            BigDecimal net,
            BigDecimal maxDD,
            BigDecimal endingQuantity,
            Direction endingDirection,
            int candleCount
    ) {}

    public record TradeResult(
            int no, long time, long exitTime, Direction direction, BigDecimal quantity,
            BigDecimal entry, BigDecimal exit, BigDecimal target, BigDecimal sl,
            String reason, boolean bothHit, BigDecimal points, BigDecimal pnl,
            BigDecimal actualLoss, BigDecimal recoveryQty, BigDecimal nextQty,
            String nextDirection, String transitionRule
    ) {}
}
