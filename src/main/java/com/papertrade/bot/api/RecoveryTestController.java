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
        if (steps < 1 || steps > 1000) throw new IllegalArgumentException("steps must be 1..1000");
        StrategyConfig config = new StrategyConfig(startingQuantity, BigDecimal.ONE, stopLossPoints, recoveryPoints);
        List<Step> result = new ArrayList<>();
        BigDecimal qty = startingQuantity;
        Direction direction = Direction.BUY;
        for (int i = 1; i <= steps; i++) {
            QuantityEngine.Transition t = QuantityEngine.afterExit(config, qty, direction, ExitType.STOP_LOSS);
            result.add(new Step(i, qty, t.actualLoss(), t.recoveryQuantity(), t.nextQuantity(), direction, t.nextDirection()));
            qty = t.nextQuantity();
            direction = t.nextDirection();
        }
        return result;
    }

    @GetMapping("/recovery-regression")
    public Regression regression() {
        List<Step> steps = recoveryTest(new BigDecimal("0.01"), new BigDecimal("250"), new BigDecimal("400"), 12);
        String[] expected = {"0.02","0.04","0.07","0.12","0.20","0.33","0.54","0.88","1.43","2.33","3.79","6.16"};
        for (int i = 0; i < expected.length; i++) {
            if (steps.get(i).nextQuantity().compareTo(new BigDecimal(expected[i])) != 0)
                throw new IllegalStateException("Recovery regression failed at step " + (i + 1));
        }
        return new Regression(true, expected, "0.01 -> 0.02 -> 0.04 -> 0.07 -> 0.12 -> 0.20 -> 0.33 -> 0.54 -> 0.88 -> 1.43 -> 2.33 -> 3.79 -> 6.16");
    }

    public record Step(int step, BigDecimal previousQuantity, BigDecimal actualLoss,
                       BigDecimal recoveryQuantity, BigDecimal nextQuantity,
                       Direction direction, Direction nextDirection) {}
    public record Regression(boolean pass, String[] expectedNextQuantities, String chain) {}
}
