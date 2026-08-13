# Trading Bot V15 — FINAL COMPLETE STATUS

This package is the deployable V15 project.

## Included
- Spring Boot Java 17 backend
- Deterministic `BigDecimal` quantity/recovery engine
- Exact BUY/SELL price-movement and quantity-adjusted P/L engine
- JPA entities/repositories for strategy configs and backtest runs/trades
- H2 development database foundation
- Historical Delta candle backtest UI
- Recovery-chain and P/L regression tests
- Dockerfile for Render
- iPhone/GitHub deployment guide

## Critical strategy regression
Starting quantity `0.01`, SL `250`, Recovery `400`:

`0.01 STOP -> 0.02 -> 0.04 -> 0.07`

Any TARGET resets the NEXT quantity to `0.01` and flips direction.

## P/L regression
- SELL 0.01: 62894 -> 63144 = Move -250, P/L -2.5
- BUY 0.02: 63374.5 -> 63124.5 = Move -250, P/L -5
- SELL 0.04: 63081 -> 63331 = Move -250, P/L -10
- BUY 0.07: 63272 -> 63772 = Move +500, P/L +35

## Build verification
Run:

```bash
mvn test
mvn spring-boot:run
```

## Render
Use a Web Service with Docker runtime and the repository root as the Dockerfile location. The app listens on `PORT` when Render supplies it, otherwise 10000.
