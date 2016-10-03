package me.jiangcai.dating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

/**
 * @author CJ
 */
public interface UserRepository extends JpaRepository<User, String> {
}
