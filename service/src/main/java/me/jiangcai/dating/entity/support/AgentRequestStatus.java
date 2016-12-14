package me.jiangcai.dating.entity.support;

/**
 * 代理商请求的几个状态
 *
 * @author CJ
 */
public enum AgentRequestStatus {
    requested,
    reject,
    forward,
    accept;

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
