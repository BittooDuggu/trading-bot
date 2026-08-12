package com.papertrade.bot.api;

import com.papertrade.bot.strategy.QuantityEngine;
import com.papertrade.bot.strategy.StrategyConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin(origins = "*")
public class StrategyController {

    @PostMapping("/next")
    public ResponseEntity<QuantityEngine.Transition> next(
            @RequestBody TransitionRequest request
    ) {
        StrategyConfig config = new StrategyConfig(
                request.startingQuantity(),
                request.targetPoints(),
                request.stopLossPoints(),
                request.recoveryPoints()
        );

        return ResponseEntity.ok(
                QuantityEngine.afterExit(
                        config,
                        request.previousQuantity(),
                        request.previousDirection(),
                        request.exitType()
                )
        );
    }
}
