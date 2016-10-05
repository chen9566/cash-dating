package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.Order;
import me.jiangcai.dating.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByOwnerOrderByStartTimeDesc(User user);

}
