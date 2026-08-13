package com.papertrade.bot.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Single source of truth for historical strategy simulation.
 *
 * The browser only supplies historical candles and configuration. All
 * entry/exit, P/L and quantity transitions are calculated here with
 * BigDecimal and QuantityEngine.
 */
public final class BacktestEngine {
    private BacktestEngine() {}

    public enum BothHitRule { TARGET_FIRST, STOP_LOSS_FIRST }

    public record Candle(
            long time,
            BigDecimal open,
            BigDecimal high,
            BigDecimal low,
            BigDecimal close,
            BigDecimal volume
    ) {}

    public record Config(
            BigDecimal startingQuantity,
            BigDecimal targetPoints,
            BigDecimal stopLossPoints,
            BigDecimal recoveryPoints,
            Direction firstDirection,
            BothHitRule bothHitRule
    ) {
        public Config {
            if (firstDirection == null) throw new IllegalArgumentException("firstDirection is required");
            if (bothHitRule == null) throw new IllegalArgumentException("bothHitRule is required");
        }
        public StrategyConfig strategyConfig() {
            return new StrategyConfig(startingQuantity, targetPoints, stopLossPoints, recoveryPoints);
        }
    }

    public record Trade(
            int number,
            long entryTime,
            long exitTime,
            Direction direction,
            BigDecimal quantity,
            BigDecimal entryPrice,
            BigDecimal exitPrice,
            BigDecimal targetPrice,
            BigDecimal stopLossPrice,
            ExitType exitType,
            boolean bothLevelsTouched,
            BigDecimal movePoints,
            BigDecimal pnl,
            BigDecimal actualLoss,
            BigDecimal rawRecoveryQuantity,
            BigDecimal recoveryQuantity,
            BigDecimal nextQuantity,
            Direction nextDirection
    ) {}

    public record Result(
            List<Trade> trades,
            BigDecimal netPnl,
            BigDecimal maxDrawdown,
            BigDecimal endingQuantity,
            Direction endingDirection
    ) {}

    public static Result run(List<Candle> candles, Config config) {
        if (candles == null || candles.size() < 2) {
            throw new IllegalArgumentException("At least two candles are required");
        }
        StrategyConfig strategyConfig = config.strategyConfig();
        List<Trade> trades = new ArrayList<>();
        Direction direction = config.firstDirection();
        BigDecimal quantity = strategyConfig.startingQuantity().setScale(strategyConfig.quantityScale(), RoundingMode.UNNECESSARY);
        BigDecimal net = BigDecimal.ZERO;
        BigDecimal peak = BigDecimal.ZERO;
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        int i = 0;
        while (i < candles.size()) {
            Candle entryCandle = candles.get(i);
            if (entryCandle == null || entryCandle.open() == null) { i++; continue; }

            quantity = quantity.setScale(strategyConfig.quantityScale(), RoundingMode.UNNECESSARY);
            BigDecimal entry = entryCandle.open();
            BigDecimal target = direction == Direction.BUY
                    ? entry.add(strategyConfig.targetPoints())
                    : entry.subtract(strategyConfig.targetPoints());
            BigDecimal stop = direction == Direction.BUY
                    ? entry.subtract(strategyConfig.stopLossPoints())
                    : entry.add(strategyConfig.stopLossPoints());

            ExitHit hit = null;
            int exitIndex = -1;
            for (int j = i; j < candles.size(); j++) {
                ExitHit candidate = checkExit(direction, target, stop, candles.get(j), config.bothHitRule());
                if (candidate != null) { hit = candidate; exitIndex = j; break; }
            }
            if (hit == null) break;

            Candle exitCandle = candles.get(exitIndex);
            BigDecimal move = TradePnlEngine.priceMovePoints(direction, entry, hit.price());
            BigDecimal pnl = TradePnlEngine.pnlValue(direction, entry, hit.price(), quantity);
            QuantityEngine.Transition transition = QuantityEngine.afterExit(
                    strategyConfig, quantity, direction, hit.exitType());

            net = net.add(pnl);
            if (net.compareTo(peak) > 0) peak = net;
            BigDecimal drawdown = peak.subtract(net);
            if (drawdown.compareTo(maxDrawdown) > 0) maxDrawdown = drawdown;

            trades.add(new Trade(
                    trades.size() + 1,
                    entryCandle.time(),
                    exitCandle.time(),
                    direction,
                    quantity,
                    entry,
                    hit.price(),
                    target,
                    stop,
                    hit.exitType(),
                    hit.bothLevelsTouched(),
                    move,
                    pnl,
                    transition.actualLoss(),
                    transition.actualLoss().compareTo(BigDecimal.ZERO) > 0
                            ? transition.actualLoss().divide(strategyConfig.recoveryPoints(), 18, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO,
                    transition.recoveryQuantity(),
                    transition.nextQuantity(),
                    transition.nextDirection()
            ));

            quantity = transition.nextQuantity();
            direction = transition.nextDirection();
            i = exitIndex + 1;
        }

        audit(trades, strategyConfig, config.firstDirection());
        return new Result(List.copyOf(trades), net, maxDrawdown, quantity, direction);
    }

    private record ExitHit(ExitType exitType, BigDecimal price, boolean bothLevelsTouched) {}

    private static ExitHit checkExit(Direction direction, BigDecimal target, BigDecimal stop,
                                     Candle candle, BothHitRule bothHitRule) {
        if (candle == null || candle.high() == null || candle.low() == null) return null;
        boolean hitTarget = direction == Direction.BUY
                ? candle.high().compareTo(target) >= 0
                : candle.low().compareTo(target) <= 0;
        boolean hitStop = direction == Direction.BUY
                ? candle.low().compareTo(stop) <= 0
                : candle.high().compareTo(stop) >= 0;

        if (hitTarget && hitStop) {
            return bothHitRule == BothHitRule.TARGET_FIRST
                    ? new ExitHit(ExitType.TARGET, target, true)
                    : new ExitHit(ExitType.STOP_LOSS, stop, true);
        }
        if (hitStop) return new ExitHit(ExitType.STOP_LOSS, stop, false);
        if (hitTarget) return new ExitHit(ExitType.TARGET, target, false);
        return null;
    }

    private static void audit(List<Trade> trades, StrategyConfig config, Direction firstDirection) {
        Direction expectedDirection = firstDirection;
        BigDecimal expectedQuantity = config.startingQuantity();
        for (Trade t : trades) {
            if (t.direction() != expectedDirection) {
                throw new IllegalStateException("Direction-chain audit failed at trade #" + t.number());
            }
            if (t.quantity().compareTo(expectedQuantity) != 0) {
                throw new IllegalStateException("Quantity-chain audit failed at trade #" + t.number()
                        + ": expected " + expectedQuantity + " got " + t.quantity());
            }
            expectedDirection = t.nextDirection();
            expectedQuantity = t.nextQuantity();
        }
    }
}
