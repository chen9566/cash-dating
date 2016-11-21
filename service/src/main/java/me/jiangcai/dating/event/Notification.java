package me.jiangcai.dating.event;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.notify.NotifyType;

import java.util.Objects;

/**
 * 通知
 *
 * @author CJ
 */
@Setter
@Getter
public class Notification {
    private final User user;
    private final NotifyType type;
    private final String uri;
    private final Object[] vars;
    private final Object key;

    /**
     * @param user 接收者
     * @param type 类型
     * @param uri  相关uri 可选
     * @param key  唯一值,为了避免重发,在2分钟内不会发布重复信息
     * @param vars 变量
     */
    public Notification(User user, NotifyType type, String uri, Object key, Object... vars) {
        this.key = key;
        this.user = user;
        this.type = type;
        this.uri = uri;
        this.vars = vars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return Objects.equals(user, that.user) &&
                type == that.type &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, type, key);
    }
}
