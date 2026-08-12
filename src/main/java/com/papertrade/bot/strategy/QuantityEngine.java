package com.papertrade.bot.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The single source of truth for quantity transitions.
 *
 * STOP LOSS:
 *   actualLoss = previousQty * stopLossPoints
 *   recoveryQtyRaw = actualLoss / recoveryPoints
 *   recoveryQty = ROUND UP to starting-quantity precision
 *   nextQty = previousQty + recoveryQty
 *
 * TARGET:
 *   nextQty = startingQuantity
 *
 * Direction flips after every exit.
 *
 * No double/float is used anywhere in this calculation.
 */
public final class QuantityEngine {

    private QuantityEngine() {}

    public static Transition afterExit(
            StrategyConfig config,
            BigDecimal previousQuantity,
            Direction previousDirection,
            ExitType exitType
    ) {
        requirePositive(previousQuantity, "previousQuantity");

        Direction nextDirection = previousDirection.opposite();

        if (exitType == ExitType.TARGET) {
            return new Transition(
                    config.startingQuantity(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    nextDirection
            );
        }

        BigDecimal actualLoss = previousQuantity.multiply(config.stopLossPoints());
        BigDecimal rawRecovery = actualLoss.divide(
                config.recoveryPoints(), 18, RoundingMode.HALF_UP
        );

        BigDecimal recoveryQuantity = roundUpToStartingPrecision(
                rawRecovery, config.quantityScale()
        );

        BigDecimal nextQuantity = previousQuantity.add(recoveryQuantity)
                .setScale(config.quantityScale(), RoundingMode.UNNECESSARY);

        return new Transition(
                nextQuantity,
                actualLoss,
                recoveryQuantity,
                nextDirection
        );
    }

    private static BigDecimal roundUpToStartingPrecision(
            BigDecimal value, int scale
    ) {
        if (value.signum() <= 0) {
            return BigDecimal.ZERO.setScale(scale);
        }
        return value.setScale(scale, RoundingMode.CEILING);
    }

    private static void requirePositive(BigDecimal value, String name) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(name + " must be > 0");
        }
    }

    public record Transition(
            BigDecimal nextQuantity,
            BigDecimal actualLoss,
            BigDecimal recoveryQuantity,
            Direction nextDirection
    ) {}
}
