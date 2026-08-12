package com.papertrade.bot.strategy;

public enum Direction {
    BUY,
    SELL;

    public Direction opposite() {
        return this == BUY ? SELL : BUY;
    }
}
