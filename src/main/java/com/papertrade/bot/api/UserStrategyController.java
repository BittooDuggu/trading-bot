package com.papertrade.bot.api;
import com.papertrade.bot.auth.*; import com.papertrade.bot.db.*; import jakarta.servlet.http.HttpSession; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/user")
public class UserStrategyController { private final StrategyRepository strategies; private final UserRepository users; public UserStrategyController(StrategyRepository s,UserRepository u){strategies=s;users=u;}
 @GetMapping("/strategies") public List<?> mine(HttpSession s){Object id=s.getAttribute("USER_ID"); if(id==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); UserEntity u=users.findById((Long)id).orElseThrow(); return strategies.findByAssignedUsersContainingAndEnabledTrue(u).stream().map(e->Map.of("id",e.getId(),"name",e.getName(),"type",e.getType().name(),"market",e.getMarket(),"symbol",e.getSymbol(),"startingQuantity",e.getStartingQuantity(),"targetPoints",e.getTargetPoints(),"stopLossPoints",e.getStopLossPoints(),"recoveryPoints",e.getRecoveryPoints())).toList(); }
}
