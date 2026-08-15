package com.papertrade.bot.strategy.type;
import com.papertrade.bot.db.StrategyType;
public class ForexStrategy implements MarketStrategy { public StrategyType type(){return StrategyType.FOREX;} public String engineClass(){return getClass().getSimpleName();} }
