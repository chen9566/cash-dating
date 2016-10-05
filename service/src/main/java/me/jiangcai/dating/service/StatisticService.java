package me.jiangcai.dating.service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
     * @param openId
     * @return 实时余额
     */
    @Transactional(readOnly = true)
    BigDecimal balance(String openId);

}
