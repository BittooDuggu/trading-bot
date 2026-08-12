package com.papertrade.bot.api;

import com.papertrade.bot.strategy.Direction;
import com.papertrade.bot.strategy.TradePnlEngine;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin(origins = "*")
public class PnlController {
    @PostMapping("/pnl")
    public PnlResponse pnl(@RequestBody PnlRequest request) {
        BigDecimal move = TradePnlEngine.priceMovePoints(
                request.direction(), request.entry(), request.exit());
        BigDecimal pnl = TradePnlEngine.pnlValue(
                request.direction(), request.entry(), request.exit(), request.quantity());
        return new PnlResponse(move, pnl);
    }

    public record PnlRequest(Direction direction, BigDecimal entry, BigDecimal exit, BigDecimal quantity) {}
    public record PnlResponse(BigDecimal priceMovePoints, BigDecimal pnlValue) {}
}
