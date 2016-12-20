package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.ManageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByOpenId(String id);

    User findByMobileNumber(String mobile);

    User findByInviteCode(String code);

    long countByGuideUser_OpenIdAndMobileNumberNotNull(String openId);

    List<User> findByEnabledTrueAndManageStatus(ManageStatus manageStatus);
}
