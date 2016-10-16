package me.jiangcai.dating.model.support;

/**
 * 订单流水的状态
 *
 * @author CJ
 */
public enum OrderFlowStatus {
    success,
    /**
     * 交易中
     */
    transferring,
    /**
     * 需要卡
     */
    cardRequired,
    /**
     * 失败
     */
    failed
}
