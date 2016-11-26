package me.jiangcai.dating.entity.support;

/**
 * 借款请求的几个状态
 *
 * @author CJ
 */
public enum LoanRequestStatus {
    requested,
    reject,
    forward,
    accept,
    /**
     * 尚未提交的请求
     */
    init
}
