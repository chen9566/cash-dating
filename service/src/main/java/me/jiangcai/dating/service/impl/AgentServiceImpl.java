package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.AgentRequestStatus;
import me.jiangcai.dating.exception.RequestedException;
import me.jiangcai.dating.repository.AgentRequestRepository;
import me.jiangcai.dating.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author CJ
 */
@Service
public class AgentServiceImpl implements AgentService {
    @Autowired
    private AgentRequestRepository agentRequestRepository;

    @Override
    public List<AgentRequest> waitingList() {
        return agentRequestRepository.findByProcessStatusOrderByCreatedTime(AgentRequestStatus.requested
                , AgentRequestStatus.forward);
    }

    @Override
    public AgentRequest newRequest(User user, String name, String mobile) throws RequestedException {
        if (user.getAgentInfo() != null)
            throw new IllegalStateException();
        if (agentRequestRepository.findByFromAndProcessStatus(user, AgentRequestStatus.requested
                , AgentRequestStatus.forward) != null)
            throw new RequestedException("您的申请正在处理中,重复申请会拖慢您的申请进度。");

        AgentRequest request = new AgentRequest();
        request.setName(name);
        request.setCreatedTime(LocalDateTime.now());
        request.setFrom(user);
        request.setMobileNumber(mobile);
        request.setProcessStatus(AgentRequestStatus.requested);
        return agentRequestRepository.save(request);
    }
}
