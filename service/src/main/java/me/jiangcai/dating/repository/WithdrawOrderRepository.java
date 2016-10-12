package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.WithdrawOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface WithdrawOrderRepository extends JpaRepository<WithdrawOrder, String> {

    List<WithdrawOrder> findByOwnerOrderByStartTimeDesc(User user);
}
