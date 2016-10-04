package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface OrderRepository extends JpaRepository<Order, String> {

}
