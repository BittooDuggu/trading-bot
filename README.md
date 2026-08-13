# Trading Bot V16 — FINAL

This version locks the strategy transition and P/L semantics so the browser backtest and future Java backend use the same rules.

## Final strategy rules

### TARGET
- Close the trade.
- Flip direction.
- Reset next quantity to Starting Quantity.

### STOP LOSS
- Close the trade.
- Flip direction.
- `actualLoss = previousQuantity × StopLossPoints`
- `rawRecoveryQuantity = actualLoss ÷ RecoveryPoints`
- Round recovery quantity UP to the decimal precision of Starting Quantity.
- `nextQuantity = previousQuantity + recoveryQuantity`
- Never allow zero/negative next quantity.

## P/L rule — important

The UI now keeps two different values separate:

- **Move** = signed price movement in raw points.
- **P/L** = `Move × Quantity` (quantity-adjusted points-value).

Examples:

- SELL 0.01: 62894 → 63144 = Move **-250**, P/L **-2.5**
- BUY 0.02: 63374.5 → 63124.5 = Move **-250**, P/L **-5**
- SELL 0.04: 63081 → 63331 = Move **-250**, P/L **-10**
- BUY 0.07: 63272 → 63772 = Move **+500**, P/L **+35**

The old misleading label `P/L -250 points` is no longer used for these quantity-sized trades.

## Verification

Java uses `BigDecimal`; no floating point is used in the quantity/recovery engine or P/L engine.

Run:

```bash
mvn test
mvn spring-boot:run
```

Open:

`http://localhost:8080/`

The browser fetches historical Delta candles, then sends them to the Java server-side backtest engine. Quantity transitions, exit selection, and P/L are therefore calculated by one BigDecimal-based implementation. The old browser calculation remains only as legacy code and is not used by the backtest. The server also exposes:

- `POST /api/strategy/next` — quantity transition
- `GET /api/strategy/recovery-test` — deterministic recovery chain
- `POST /api/strategy/pnl` — exact price-movement and quantity-adjusted P/L calculation

## Database foundation

JPA entities/repositories are included for strategy configs and backtest runs/trades. H2 is used for development; PostgreSQL can be configured later for production.

## GitHub / Render

The project contains a Spring Boot backend and the static browser frontend under `src/main/resources/static/index.html`, so one deployed Spring Boot service can serve the UI and APIs from the same URL.
