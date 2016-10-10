package me.jiangcai.dating;

import me.jiangcai.dating.service.SystemService;

/**
 * 利益的分配者
 *
 * @author CJ
 */
public interface ProfitSplit {

    /**
     * 2个分配比例相加绝对不可以大于或者等于1
     *
     * @param systemService
     * @return 合伙人利润分配比例, 没有合伙人可以返回{@link java.lang.Double#NaN}
     */
    double agentProfileRate(SystemService systemService);

    /**
     * 2个分配比例相加绝对不可以大于或者等于1
     *
     * @param systemService
     * @return 发展人利润分配比例, 没有合伙人可以返回{@link java.lang.Double#NaN}
     */
    double guideProfileRate(SystemService systemService);

    /**
     * 检查是否符合约定
     *
     * @param systemService
     */
    default void validateProfitSplit(SystemService systemService) {
        double agent = agentProfileRate(systemService);
        if (Double.isNaN(agent))
            agent = 0;
        double guide = guideProfileRate(systemService);
        if (Double.isNaN(guide))
            guide = 0;
        if (agent + guide >= 1)
            throw new InternalError("利润分配错误。" + (agent + guide));
    }
}
