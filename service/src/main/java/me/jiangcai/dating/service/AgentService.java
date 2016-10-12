package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.AgentInfo;
import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.exception.RequestedException;
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

    /**
     * 新加申请
     *
     * @param user   用户
     * @param name   真实名字
     * @param mobile 电话
     * @return 新添加的申请
     * @throws RequestedException    已经申请了
     * @throws IllegalStateException 没有必要;比如它已经是代理商了
     */
    @Transactional
    AgentRequest newRequest(User user, String name, String mobile) throws RequestedException;

    /**
     * 让它成为合伙人,它所发展的人也将成为它的区域???? TODO 这个还需要确认。
     *
     * @param user 成为合伙人的人
     */
    @Transactional
    AgentInfo makeAgent(User user);
}
