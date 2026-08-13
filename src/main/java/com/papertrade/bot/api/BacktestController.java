package com.papertrade.bot.api;

import com.papertrade.bot.strategy.BacktestEngine;
import com.papertrade.bot.strategy.Direction;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/backtest")
@CrossOrigin(origins = "*")
public class BacktestController {

    @PostMapping("/run")
    public BacktestEngine.Result run(@RequestBody BacktestRequest request) {
        if (request == null || request.candles() == null || request.candles().size() < 2) {
            throw new IllegalArgumentException("At least two historical candles are required");
        }
        BacktestEngine.Config config = new BacktestEngine.Config(
                request.startingQuantity(), request.targetPoints(), request.stopLossPoints(),
                request.recoveryPoints(), request.firstDirection(), request.bothHitRule());
        return BacktestEngine.run(request.candles(), config);
    }

    public record BacktestRequest(
            BigDecimal startingQuantity,
            BigDecimal targetPoints,
            BigDecimal stopLossPoints,
            BigDecimal recoveryPoints,
            Direction firstDirection,
            BacktestEngine.BothHitRule bothHitRule,
            List<BacktestEngine.Candle> candles
    ) {}
}
