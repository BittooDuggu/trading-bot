package com.papertrade.bot.api;

import com.papertrade.bot.auth.UserEntity;
import com.papertrade.bot.auth.UserRepository;
import com.papertrade.bot.db.StrategyEntity;
import com.papertrade.bot.db.StrategyRepository;
import com.papertrade.bot.db.StrategyType;
import com.papertrade.bot.strategy.type.MarketStrategyFactory;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class StrategyAdminController {
    private final StrategyRepository strategies;
    private final UserRepository users;
    private final MarketStrategyFactory factory;

    public StrategyAdminController(StrategyRepository strategies,
                                   UserRepository users,
                                   MarketStrategyFactory factory) {
        this.strategies = strategies;
        this.users = users;
        this.factory = factory;
    }

    private UserEntity requireAdmin(HttpSession session) {
        Object idValue = session.getAttribute("USER_ID");
        Object roleValue = session.getAttribute("ROLE");
        if (!(idValue instanceof Long) || !"ADMIN".equals(String.valueOf(roleValue))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
        return users.findById((Long) idValue)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin user not found"));
    }

    @GetMapping("/users")
    public List<Map<String, Object>> listUsers(HttpSession session) {
        requireAdmin(session);
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserEntity user : users.findAll()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", user.getId());
            row.put("name", user.getName());
            row.put("email", user.getEmail());
            row.put("role", user.getRole().name());
            result.add(row);
        }
        return result;
    }

    @GetMapping("/strategies")
    public List<Map<String, Object>> listStrategies(HttpSession session) {
        requireAdmin(session);
        List<Map<String, Object>> result = new ArrayList<>();
        for (StrategyEntity strategy : strategies.findAll()) {
            result.add(toDto(strategy));
        }
        return result;
    }

    @PostMapping("/strategies")
    public ResponseEntity<Map<String, Object>> create(@RequestBody StrategyRequest request,
                                                        HttpSession session) {
        requireAdmin(session);
        StrategyEntity entity = new StrategyEntity();
        apply(entity, request);
        strategies.save(entity);
        return ResponseEntity.ok(toDto(entity));
    }

    @PutMapping("/strategies/{id}")
    public Map<String, Object> update(@PathVariable Long id,
                                      @RequestBody StrategyRequest request,
                                      HttpSession session) {
        requireAdmin(session);
        StrategyEntity entity = strategies.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Strategy not found"));
        apply(entity, request);
        return toDto(strategies.save(entity));
    }

    @PostMapping("/strategies/{id}/assign/{userId}")
    public ResponseEntity<Map<String, Object>> assign(@PathVariable Long id,
                                                       @PathVariable Long userId,
                                                       HttpSession session) {
        requireAdmin(session);
        StrategyEntity strategy = strategies.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Strategy not found"));
        UserEntity user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        strategy.getAssignedUsers().add(user);
        strategies.save(strategy);
        return ResponseEntity.ok(toDto(strategy));
    }

    @DeleteMapping("/strategies/{id}/assign/{userId}")
    public ResponseEntity<Map<String, Object>> unassign(@PathVariable Long id,
                                                         @PathVariable Long userId,
                                                         HttpSession session) {
        requireAdmin(session);
        StrategyEntity strategy = strategies.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Strategy not found"));
        strategy.getAssignedUsers().removeIf(user -> userId.equals(user.getId()));
        strategies.save(strategy);
        return ResponseEntity.ok(toDto(strategy));
    }

    private void apply(StrategyEntity entity, StrategyRequest request) {
        if (request == null || request.name() == null || request.name().isBlank() || request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and type are required");
        }
        if (request.market() == null || request.market().isBlank()
                || request.symbol() == null || request.symbol().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Market and symbol are required");
        }
        if (request.startingQuantity() == null || request.startingQuantity().signum() <= 0
                || request.targetPoints() == null || request.targetPoints().signum() <= 0
                || request.stopLossPoints() == null || request.stopLossPoints().signum() <= 0
                || request.recoveryPoints() == null || request.recoveryPoints().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Starting quantity, target, stop loss and recovery points must be positive");
        }

        // Validates that the requested market type has a registered Java strategy class.
        factory.get(request.type());

        entity.setName(request.name().trim());
        entity.setType(request.type());
        entity.setMarket(request.market().trim());
        entity.setSymbol(request.symbol().trim().toUpperCase());
        entity.setStartingQuantity(request.startingQuantity());
        entity.setTargetPoints(request.targetPoints());
        entity.setStopLossPoints(request.stopLossPoints());
        entity.setRecoveryPoints(request.recoveryPoints());
        entity.setEnabled(request.enabled() == null || request.enabled());
    }

    private Map<String, Object> toDto(StrategyEntity entity) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", entity.getId());
        result.put("name", entity.getName());
        result.put("type", entity.getType().name());
        result.put("market", entity.getMarket());
        result.put("symbol", entity.getSymbol());
        result.put("startingQuantity", entity.getStartingQuantity());
        result.put("targetPoints", entity.getTargetPoints());
        result.put("stopLossPoints", entity.getStopLossPoints());
        result.put("recoveryPoints", entity.getRecoveryPoints());
        result.put("enabled", entity.isEnabled());
        result.put("engineClass", factory.get(entity.getType()).engineClass());

        List<Long> assignedUserIds = new ArrayList<>();
        for (UserEntity user : entity.getAssignedUsers()) {
            assignedUserIds.add(user.getId());
        }
        result.put("assignedUserIds", assignedUserIds);
        return result;
    }

    public record StrategyRequest(
            String name,
            StrategyType type,
            String market,
            String symbol,
            BigDecimal startingQuantity,
            BigDecimal targetPoints,
            BigDecimal stopLossPoints,
            BigDecimal recoveryPoints,
            Boolean enabled
    ) {}
}
