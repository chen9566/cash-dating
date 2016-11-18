package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.model.NotifyMessageModel;
import me.jiangcai.dating.notify.NotifyType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
