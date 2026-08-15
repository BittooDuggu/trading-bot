package com.papertrade.bot.strategy.type;
import com.papertrade.bot.db.StrategyType;
public class CryptoStrategy implements MarketStrategy { public StrategyType type(){return StrategyType.CRYPTO;} public String engineClass(){return getClass().getSimpleName();} }
