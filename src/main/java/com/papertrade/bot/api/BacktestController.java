package com.papertrade.bot.api;

import com.papertrade.bot.backtest.BacktestEngine;
import com.papertrade.bot.backtest.BacktestRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backtest")
@CrossOrigin(origins = "*")
public class BacktestController {
    @PostMapping("/run")
    public BacktestEngine.Result run(@RequestBody BacktestRequest request) {
        return BacktestEngine.run(request);
    }
}
