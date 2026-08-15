package com.papertrade.bot.api;

import com.papertrade.bot.auth.UserEntity;
import com.papertrade.bot.auth.UserRepository;
import com.papertrade.bot.db.StrategyEntity;
import com.papertrade.bot.db.StrategyRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserStrategyController {
    private final StrategyRepository strategies;
    private final UserRepository users;

    public UserStrategyController(StrategyRepository strategies, UserRepository users) {
        this.strategies = strategies;
        this.users = users;
    }

    @GetMapping("/strategies")
    public List<Map<String, Object>> mine(HttpSession session) {
        Object idValue = session.getAttribute("USER_ID");
        if (!(idValue instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        UserEntity user = users.findById((Long) idValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<Map<String, Object>> result = new ArrayList<>();
        for (StrategyEntity strategy : strategies.findByAssignedUsersContainingAndEnabledTrue(user)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", strategy.getId());
            row.put("name", strategy.getName());
            row.put("type", strategy.getType().name());
            row.put("market", strategy.getMarket());
            row.put("symbol", strategy.getSymbol());
            row.put("startingQuantity", strategy.getStartingQuantity());
            row.put("targetPoints", strategy.getTargetPoints());
            row.put("stopLossPoints", strategy.getStopLossPoints());
            row.put("recoveryPoints", strategy.getRecoveryPoints());
            result.add(row);
        }
        return result;
    }
}
