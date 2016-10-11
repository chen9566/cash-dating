package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface UserOrderRepository extends JpaRepository<UserOrder, String> {
}
