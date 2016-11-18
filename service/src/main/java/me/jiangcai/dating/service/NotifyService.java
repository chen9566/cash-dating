package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.NotifyMessageParameter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.model.NotifyMessageModel;
import me.jiangcai.dating.notify.NotifyType;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 通知服务,通知都为非核心业务
 *
 * @author CJ
 */
public interface NotifyService {

    /**
     * @return 当前消息模板, 包括未设置的模板
     */
    @Transactional(readOnly = true)
    List<NotifyMessageModel> allTemplate();

    /**
     * @param type 业务
     * @return 消息, null如果未设置
     */
    @Transactional(readOnly = true)
    NotifyMessage forType(NotifyType type);

    /**
     * 保存一个
     *
     * @param message 原来的
     * @return 新的
     */
    @Transactional
    NotifyMessage save(NotifyMessage message);

    @EventListener(Notification.class)
    void sendMessage(Notification notification);

    /**
     * 实际的消息发送者
     *
     * @param user       收到通知的人
     * @param url        可以为null
     * @param message    相关业务消息
     * @param parameters 模板参数
     * @param vars       业务参数
     */
    void sendMessage(User user, String url, NotifyMessage message, Set<NotifyMessageParameter> parameters, Object... vars);
}
