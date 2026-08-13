package com.papertrade.bot.db;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "backtest_trades")
public class BacktestTradeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long runId;

    @Column(nullable = false)
    private int tradeNumber;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal entryPrice;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal exitPrice;

    @Column(nullable = false)
    private String exitType;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal pointsPnl;

    @Column(nullable = false)
    private OffsetDateTime entryTime;

    @Column(nullable = false)
    private OffsetDateTime exitTime;

    public Long getId() { return id; }
    public Long getRunId() { return runId; }
    public int getTradeNumber() { return tradeNumber; }
    public String getDirection() { return direction; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getEntryPrice() { return entryPrice; }
    public BigDecimal getExitPrice() { return exitPrice; }
    public String getExitType() { return exitType; }
    public BigDecimal getPointsPnl() { return pointsPnl; }
    public OffsetDateTime getEntryTime() { return entryTime; }
    public OffsetDateTime getExitTime() { return exitTime; }

    public void setRunId(Long v) { this.runId = v; }
    public void setTradeNumber(int v) { this.tradeNumber = v; }
    public void setDirection(String v) { this.direction = v; }
    public void setQuantity(BigDecimal v) { this.quantity = v; }
    public void setEntryPrice(BigDecimal v) { this.entryPrice = v; }
    public void setExitPrice(BigDecimal v) { this.exitPrice = v; }
    public void setExitType(String v) { this.exitType = v; }
    public void setPointsPnl(BigDecimal v) { this.pointsPnl = v; }
    public void setEntryTime(OffsetDateTime v) { this.entryTime = v; }
    public void setExitTime(OffsetDateTime v) { this.exitTime = v; }
}
