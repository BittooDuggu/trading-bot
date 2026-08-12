package com.papertrade.bot.db;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BacktestTradeRepository extends JpaRepository<BacktestTradeEntity, Long> {
    List<BacktestTradeEntity> findByRunIdOrderByTradeNumberAsc(Long runId);
}
