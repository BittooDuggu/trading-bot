package com.papertrade.bot.api;

import com.papertrade.bot.strategy.Direction;
import com.papertrade.bot.strategy.ExitType;
import java.math.BigDecimal;

public record TransitionRequest(
        BigDecimal startingQuantity,
        BigDecimal targetPoints,
        BigDecimal stopLossPoints,
        BigDecimal recoveryPoints,
        BigDecimal previousQuantity,
        Direction previousDirection,
        ExitType exitType
) {}
