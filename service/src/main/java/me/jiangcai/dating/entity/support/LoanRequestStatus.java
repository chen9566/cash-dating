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
    init;

    public String toHtml() {
        switch (this) {
            case requested:
                return "处理中";
            case forward:
                return "转发中";
            case reject:
                return "已被拒绝";
            default:
                return "已处理";
        }
    }
}
