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
    init,
    /**
     * 项目贷款特有,正在办理合同了（已发送通知给用户了,但还是可以重复发）
     */
    contract;

    public String toHtml() {
        switch (this) {
            case requested:
                return "处理中";
            case forward:
                return "转发中";
            case reject:
                return "已被拒绝";
            case accept:
                return "等待供应商";
            default:
                return "签章和其他";
        }
    }
}
