package com.papertrade.bot.strategy.type;
import com.papertrade.bot.db.StrategyType;
public class StocksStrategy implements MarketStrategy { public StrategyType type(){return StrategyType.STOCKS;} public String engineClass(){return getClass().getSimpleName();} }
