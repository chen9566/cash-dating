package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.WithdrawOrder;
import me.jiangcai.dating.entity.support.AgentCashOrderBalanceFlow;
import me.jiangcai.dating.entity.support.BalanceFlow;
import me.jiangcai.dating.entity.support.GuideCashOrderBalanceFlow;
import me.jiangcai.dating.entity.support.WithdrawOrderStatus;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.repository.WithdrawOrderRepository;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.dating.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private WithdrawOrderRepository withdrawOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CashOrderRepository cashOrderRepository;

    @Override
    public BigDecimal totalExpense(String openId) {
        User user = userRepository.findByOpenId(openId);
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(user.getSettlementExpense());

        for (CashOrder order : orderService.findOrders(openId)) {
            // 必须得完成才可以算吧?
            if (orderService.isComplete(order.getId()))
                total = total.add(order.getAmount());
        }

        return total;
    }

    @Override
    public BigDecimal balance(String openId) {
        User user = userRepository.findByOpenId(openId);
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(user.getSettlementBalance());

        for (BalanceFlow flow : balanceFlows(openId)) {
            BigDecimal data = flow.getAmount().multiply(flow.getFlowType().toMultiply());
            total  = total.add(data);
        }

        return total;
    }

    @Override
    public List<BalanceFlow> balanceFlows(String openId) {
        User user = userRepository.findByOpenId(openId);

        ArrayList<BalanceFlow> flowArrayList = new ArrayList<>();

        // 代理商佣金部分   需要完成
        cashOrderRepository.findByOwner_AgentUserAndCompletedTrue(user).stream()
                .map(AgentCashOrderBalanceFlow::new)
                .forEach(flowArrayList::add);
        cashOrderRepository.findByOwner_GuideUserAndCompletedTrue(user).stream()
                .map(GuideCashOrderBalanceFlow::new)
                .forEach(flowArrayList::add);

        withdrawOrderRepository.findByOwnerOrderByStartTimeDesc(user).stream()
                .filter(withdrawOrder -> withdrawOrder.getProcessStatus() != WithdrawOrderStatus.cancelled)
                .forEach(flowArrayList::add);

        Collections.sort(flowArrayList, (o1, o2) -> o2.getStartTime().compareTo(o1.getStartTime()));

        return flowArrayList;
    }
}
