package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> {
}
