package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.support.NotifyMessagePK;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface NotifyMessageRepository extends JpaRepository<NotifyMessage, NotifyMessagePK> {
}
