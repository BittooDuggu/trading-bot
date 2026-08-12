package com.papertrade.bot.api;

import com.papertrade.bot.strategy.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin(origins = "*")
public class RecoveryTestController {

    @GetMapping("/recovery-test")
    public List<Step> recoveryTest(
            @RequestParam(defaultValue = "0.01") BigDecimal startingQuantity,
            @RequestParam(defaultValue = "250") BigDecimal stopLossPoints,
            @RequestParam(defaultValue = "400") BigDecimal recoveryPoints,
            @RequestParam(defaultValue = "12") int steps
    ) {
        StrategyConfig config = new StrategyConfig(
                startingQuantity,
                BigDecimal.ONE,
                stopLossPoints,
                recoveryPoints
        );

        List<Step> result = new ArrayList<>();
        BigDecimal qty = startingQuantity;
        Direction direction = Direction.BUY;

        for (int i = 1; i <= steps; i++) {
            QuantityEngine.Transition t = QuantityEngine.afterExit(
                    config, qty, direction, ExitType.STOP_LOSS
            );
            result.add(new Step(
                    i, qty, t.actualLoss(), t.recoveryQuantity(),
                    t.nextQuantity(), direction, t.nextDirection()
            ));
            qty = t.nextQuantity();
            direction = t.nextDirection();
        }
        return result;
    }

    public record Step(
            int step,
            BigDecimal previousQuantity,
            BigDecimal actualLoss,
            BigDecimal recoveryQuantity,
            BigDecimal nextQuantity,
            Direction direction,
            Direction nextDirection
    ) {}
}
