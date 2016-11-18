package me.jiangcai.dating.event;

import lombok.Data;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.notify.NotifyType;

/**
 * 通知
 *
 * @author CJ
 */
@Data
public class Notification {
    private final User user;
    private final NotifyType type;
    private final String uri;
    private final Object[] vars;

    public Notification(User user, NotifyType type, String uri, Object... vars) {
        this.user = user;
        this.type = type;
        this.uri = uri;
        this.vars = vars;
    }
}
