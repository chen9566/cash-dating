package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author CJ
 */
public interface UserOrderRepository extends JpaRepository<UserOrder, String>, JpaSpecificationExecutor<UserOrder> {
}
