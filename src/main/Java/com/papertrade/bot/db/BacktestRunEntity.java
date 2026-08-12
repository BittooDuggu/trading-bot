package com.papertrade.bot.db;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "backtest_runs")
public class BacktestRunEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String timeframe;

    @Column(nullable = false)
    private String firstCallDirection;

    public Long getId() { return id; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getSymbol() { return symbol; }
    public String getTimeframe() { return timeframe; }
    public String getFirstCallDirection() { return firstCallDirection; }

    public void setSymbol(String v) { this.symbol = v; }
    public void setTimeframe(String v) { this.timeframe = v; }
    public void setFirstCallDirection(String v) { this.firstCallDirection = v; }
}
