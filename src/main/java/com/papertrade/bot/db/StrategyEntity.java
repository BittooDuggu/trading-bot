package com.papertrade.bot.db;

import com.papertrade.bot.auth.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name="strategies")
public class StrategyEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private StrategyType type;
 @Column(nullable=false) private String market;
 @Column(nullable=false) private String symbol;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal startingQuantity;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal targetPoints;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal stopLossPoints;
 @Column(nullable=false,precision=20,scale=8) private BigDecimal recoveryPoints;
 @Column(nullable=false) private boolean enabled=true;
 @ManyToMany @JoinTable(name="strategy_user_assignments", joinColumns=@JoinColumn(name="strategy_id"), inverseJoinColumns=@JoinColumn(name="user_id")) private Set<UserEntity> assignedUsers=new HashSet<>();
 public Long getId(){return id;} public String getName(){return name;} public StrategyType getType(){return type;} public String getMarket(){return market;} public String getSymbol(){return symbol;} public BigDecimal getStartingQuantity(){return startingQuantity;} public BigDecimal getTargetPoints(){return targetPoints;} public BigDecimal getStopLossPoints(){return stopLossPoints;} public BigDecimal getRecoveryPoints(){return recoveryPoints;} public boolean isEnabled(){return enabled;} public Set<UserEntity> getAssignedUsers(){return assignedUsers;}
 public void setName(String v){name=v;} public void setType(StrategyType v){type=v;} public void setMarket(String v){market=v;} public void setSymbol(String v){symbol=v;} public void setStartingQuantity(BigDecimal v){startingQuantity=v;} public void setTargetPoints(BigDecimal v){targetPoints=v;} public void setStopLossPoints(BigDecimal v){stopLossPoints=v;} public void setRecoveryPoints(BigDecimal v){recoveryPoints=v;} public void setEnabled(boolean v){enabled=v;}
}
