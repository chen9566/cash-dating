package me.jiangcai.dating;

import me.jiangcai.dating.model.InviteLevel;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.UserService;

import java.math.BigDecimal;

/**
 * 利益的分配者
 *
 * @author CJ
 */
public interface ProfitSplit {

    /**
     * 账面手续费率
     *
     * @param systemService
     * @param userService
     * @return 可以返回空标识使用默认
     * @deprecated 应该不好用了
     */
    BigDecimal bookProfileRate(SystemService systemService, UserService userService);

    /**
     * @param userService 用户服务
     * @return 推广激励等级; null表示没有
     */
    InviteLevel inviteLevel(UserService userService);

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

    /**
     * @return true for 使用最低账面手续费
     */
    default boolean useLowestRate() {
        return false;
    }

    /**
     * @param userService 用户服务
     * @return 确定的引导者汇率;null表示没有
     */
    BigDecimal guideRate(UserService userService);
}
