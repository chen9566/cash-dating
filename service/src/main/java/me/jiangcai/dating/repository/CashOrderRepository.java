package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author CJ
 */
public interface CashOrderRepository extends JpaRepository<CashOrder, String> {

    List<CashOrder> findByOwnerOrderByStartTimeDesc(User user);

    // 返回user作为代理商身份所获得的订单(前提是订单必须是完成的)
    List<CashOrder> findByOwner_AgentUserAndCompletedTrue(User user);

    List<CashOrder> findByOwner_AgentUserAndCompletedTrueAndThatRateConfig_AgentRateGreaterThan(User user, Number x);

    // 返回user作为引导者身份所获得的订单(前提是订单必须是完成的)
    List<CashOrder> findByOwner_GuideUserAndCompletedTrue(User user);

    List<CashOrder> findByOwner_GuideUserAndCompletedTrueAndThatRateConfig_GuideRateGreaterThan(User user, Number x);

    @Query("select C,W from CashOrder C left join C.platformWithdrawalOrderSet W" +
            " where C.owner=?1 and C.completed=true" +
            " order by C.startTime desc,W.startTime desc")
    List<?> findOrderFlow(User user);

//    @Query("select concat(FUNC('year',C.startTime),FUNC('month',C.startTime)),C,W from CashOrder C left join C.platformWithdrawalOrderSet W" +
//            " where C.owner=?1 and C.completed=true" +
//            " group by concat(FUNC('year',C.startTime),FUNC('month',C.startTime)) " +
//            " order by C.startTime desc,W.startTime desc")
//    List<?> findOrderFlowMonthly(User user);

    long countByOwner_OpenIdAndCompletedTrue(String openId);

}
