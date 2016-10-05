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

    // 返回user作为代理商身份所获得的订单(前提是订单必须是完成的)
    List<CashOrder> findByOwner_AgentUserAndCompletedTrue(User user);

    // 返回user作为引导者身份所获得的订单(前提是订单必须是完成的)
    List<CashOrder> findByOwner_GuideUserAndCompletedTrue(User user);

}
