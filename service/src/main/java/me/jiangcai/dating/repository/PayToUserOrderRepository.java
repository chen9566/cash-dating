package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.PayToUserOrder;
import me.jiangcai.dating.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface PayToUserOrderRepository extends JpaRepository<PayToUserOrder, String> {

    List<PayToUserOrder> findByOwnerAndCompletedTrueOrderByStartTimeDesc(User owner);
}
