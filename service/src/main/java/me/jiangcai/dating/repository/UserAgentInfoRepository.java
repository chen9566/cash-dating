package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.UserAgentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface UserAgentInfoRepository extends JpaRepository<UserAgentInfo, Long> {
}
