package com.papertrade.bot.db;
import com.papertrade.bot.auth.UserEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface StrategyRepository extends JpaRepository<StrategyEntity,Long>{ List<StrategyEntity> findByAssignedUsersContainingAndEnabledTrue(UserEntity u); }
