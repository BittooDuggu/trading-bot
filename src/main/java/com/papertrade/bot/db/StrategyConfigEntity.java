package com.papertrade.bot.db;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "strategy_configs")
public class StrategyConfigEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal startingQuantity;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal targetPoints;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal stopLossPoints;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal recoveryPoints;

    public Long getId() { return id; }
    public BigDecimal getStartingQuantity() { return startingQuantity; }
    public BigDecimal getTargetPoints() { return targetPoints; }
    public BigDecimal getStopLossPoints() { return stopLossPoints; }
    public BigDecimal getRecoveryPoints() { return recoveryPoints; }

    public void setStartingQuantity(BigDecimal v) { this.startingQuantity = v; }
    public void setTargetPoints(BigDecimal v) { this.targetPoints = v; }
    public void setStopLossPoints(BigDecimal v) { this.stopLossPoints = v; }
    public void setRecoveryPoints(BigDecimal v) { this.recoveryPoints = v; }
}
