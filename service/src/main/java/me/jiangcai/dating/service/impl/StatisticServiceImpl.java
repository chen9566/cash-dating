package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.model.AgentCashOrderBalanceFlow;
import me.jiangcai.dating.model.BalanceFlow;
import me.jiangcai.dating.model.GuideCashOrderBalanceFlow;
import me.jiangcai.dating.model.WithdrawalBalanceFlow;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.repository.PayToUserOrderRepository;
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

    /**
     * 合伙收益
     */
    private static final int TypeAgent = 1;
    /**
     * 发展收益
     */
    private static final int TypeGuide = 1 << 1;
    /**
     * 支付收益
     */
    private static final int TypePay = 1 << 2;
    /**
     * 提现开支,用户提出的提现
     */
    private static final int TypeWithdraw = 1 << 4;
    /**
     * 提现收入（指的是用户收到的提现）
     */
    private static final int TypeWithdrawIncoming = 1 << 5;
    /**
     * 所有收入
     */
    private static final int TypeRevenue = TypeAgent | TypeGuide | TypePay;
    /**
     * 佣金收入
     */
    private static final int TypeCommission = TypeAgent | TypeGuide;
    /**
     * 所有流水
     */
    private static final int TypeAll = TypeRevenue | TypeWithdraw;
    @Autowired
    private OrderService orderService;
    @Autowired
    private WithdrawOrderRepository withdrawOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CashOrderRepository cashOrderRepository;
    @Autowired
    private PayToUserOrderRepository payToUserOrderRepository;

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
            total = total.add(data);
        }

        return total;
    }

    @Override
    public BigDecimal withdrawal(String openId) {
        // 总提现金额
        User user = userRepository.findByOpenId(openId);
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(user.getSettlementWithdrawal());

        for (BalanceFlow flow : balanceFlows(openId, TypeWithdrawIncoming)) {
            BigDecimal data = flow.getAmount().multiply(flow.getFlowType().toMultiply());
            total = total.add(data);
        }

        return total;
    }

    @Override
    public BigDecimal revenue(String openId) {
        User user = userRepository.findByOpenId(openId);
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(user.getSettlementBalance());

        for (BalanceFlow flow : balanceFlows(openId, TypeRevenue)) {
            BigDecimal data = flow.getAmount().multiply(flow.getFlowType().toMultiply());
            total = total.add(data);
        }

        return total;
    }

    private List<BalanceFlow> balanceFlows(String openId, int flag) {
        User user = userRepository.findByOpenId(openId);

        ArrayList<BalanceFlow> flowArrayList = new ArrayList<>();

        // 代理商佣金部分   需要完成
        if ((flag & TypeAgent) > 0)
            cashOrderRepository.findByOwner_AgentUserAndCompletedTrueAndThatRateConfig_AgentRateGreaterThan(user
                    , BigDecimal.ZERO)
                    .stream()
                    .map(AgentCashOrderBalanceFlow::new)
                    .forEach(flowArrayList::add);
        if ((flag & TypeGuide) > 0)
            cashOrderRepository.findByOwner_GuideUserAndCompletedTrueAndThatRateConfig_GuideRateGreaterThan(user
                    , BigDecimal.ZERO)
                    .stream()
                    .map(GuideCashOrderBalanceFlow::new)
                    .forEach(flowArrayList::add);
//        if ((flag & TypePay) > 0){
//            payToUserOrderRepository.findByOwnerAndCompleteTrueOrderByStartTimeDesc(user).stream()
//                    .map(GuideCashOrderBalanceFlow::new)
//                    .forEach(flowArrayList::add);
//        }
        if ((flag & TypeWithdrawIncoming) > 0)
            cashOrderRepository.findByOwnerAndCompletedTrueAndWithdrawalCompletedTrue(user)
                    .stream()
                    .map(WithdrawalBalanceFlow::new)
                    .forEach(flowArrayList::add);

        if ((flag & TypeWithdraw) > 0)
            withdrawOrderRepository.findByOwnerOrderByStartTimeDesc(user).stream()
                    // NOT Cancelled -> Complete or not complete and not finish
                    // Finish and Complete
                    .filter(UserOrder.reallyWithdrawalCompletedPredicate().or(UserOrder.reallyWithdrawalFinishPredicate().negate()))
                    .forEach(flowArrayList::add);

        Collections.sort(flowArrayList, (o1, o2) -> o2.getStartTime().compareTo(o1.getStartTime()));

        return flowArrayList;
    }

    @Override
    public List<BalanceFlow> balanceFlows(String openId) {
        return balanceFlows(openId, TypeAll);
    }

    @Override
    public List<BalanceFlow> commissionFlows(String openId) {
        return balanceFlows(openId, TypeCommission);
    }

    @Override
    public List<BalanceFlow> withdrawalFlows(String openId) {
        return balanceFlows(openId, TypeWithdraw);
    }

    @Override
    public long countCashOrder(String openId) {
        return cashOrderRepository.countByOwner_OpenIdAndCompletedTrue(openId);
    }

    @Override
    public long guides(String openId) {
        return userRepository.countByGuideUser_OpenIdAndMobileNumberNotNull(openId);
    }
}
