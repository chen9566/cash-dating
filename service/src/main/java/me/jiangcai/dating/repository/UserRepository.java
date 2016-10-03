package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByOpenId(String id);
}
