package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface CashOrderRepository extends JpaRepository<CashOrder, String> {

    List<CashOrder> findByOwnerOrderByStartTimeDesc(User user);

}
