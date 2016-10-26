package me.jiangcai.dating.service;

import me.jiangcai.dating.model.BalanceFlow;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 帐面金额，平台佣金，合伙人佣金，发展人佣金
 * 数据统计服务,报表相关的;
 * <ul>
 * <li>expense 开支,指的是账面金额</li>
 * <li>channel 渠道,指的是支付平台支取费用</li>
 * <li>agent 代理,指的是代理商支取费用</li>
 * <li>guide 引导者,指的是引导人支取费用</li>
 * </ul>
 *
 * @author CJ
 */
public interface StatisticService {

    /**
     * @param openId 用户openId
     * @return 实时的所有开支
     */
    @Transactional(readOnly = true)
    BigDecimal totalExpense(String openId);

    /**
     * @param openId 用户openId
     * @return 实时余额
     */
    @Transactional(readOnly = true)
    BigDecimal balance(String openId);

    /**
     * @param openId 用户openId
     * @return 实时总收入
     */
    @Transactional(readOnly = true)
    BigDecimal revenue(String openId);

    /**
     * 应该是按照时间的降序排列的
     *
     * @param openId 用户openId
     * @return 用户所有的流水
     */
    @Transactional(readOnly = true)
    List<BalanceFlow> balanceFlows(String openId);

    /**
     * @param openId 用户openId
     * @return 成功提现订单的数量
     */
    @Transactional(readOnly = true)
    long countCashOrder(String openId);
}
