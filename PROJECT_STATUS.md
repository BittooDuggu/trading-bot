# Trading Bot V16 — Server Recovery Engine

## Final architecture for this stage
- Java `QuantityEngine` is the single source of truth for STOP LOSS recovery and TARGET reset.
- Java `BacktestEngine` receives historical candles from the browser and performs the complete trade simulation.
- Browser no longer calculates recovery quantities or backtest transitions.
- `TEST RECOVERY` calls `/api/strategy/recovery-test` and displays all 12 consecutive STOP LOSS steps.
- `/api/strategy/recovery-regression` verifies the expected 12-step regression chain.

## Required regression
Starting quantity `0.01`, SL `250`, Recovery `400`:
`0.01 -> 0.02 -> 0.04 -> 0.07 -> 0.12 -> 0.20 -> 0.33 -> 0.54 -> 0.88 -> 1.43 -> 2.33 -> 3.79 -> 6.16`

## Deployment
Render should build the Dockerfile and expose the port supplied by `PORT`.
