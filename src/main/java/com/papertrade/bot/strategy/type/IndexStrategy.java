package com.papertrade.bot.strategy.type;
import com.papertrade.bot.db.StrategyType;
public class IndexStrategy implements MarketStrategy { public StrategyType type(){return StrategyType.INDEX;} public String engineClass(){return getClass().getSimpleName();} }
