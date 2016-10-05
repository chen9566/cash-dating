package me.jiangcai.dating.entity.support;

/**
 * 用户余额的流水,这里的所有语义应该是以用于余额的流水考虑的
 *
 * @author CJ
 */
public interface BalanceFlow {

    /**
     * @return 干了什么?比如是提现还是佣金
     */
    String getFlowName();

    FlowType getFlowType();

    /**
     * @return 流水金额, 必须是正数
     */
    java.math.BigDecimal getAmount();

    java.time.LocalDateTime getStartTime();

    /**
     * @return 干的事情的备注
     */
    String getComment();
}
