package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.AgentRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 代理相关的服务
 *
 * @author CJ
 */
public interface AgentService {

    /**
     * 越早提交的越先看到,所以应该是时间升序
     *
     * @return 正在等待处理的请求
     */
    @Transactional(readOnly = true)
    List<AgentRequest> waitingList();
}
