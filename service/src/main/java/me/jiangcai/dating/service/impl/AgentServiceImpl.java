package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.AgentInfo;
import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.AgentRequestStatus;
import me.jiangcai.dating.exception.RequestedException;
import me.jiangcai.dating.repository.AgentRequestRepository;
import me.jiangcai.dating.repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<AgentRequest> waitingList() {
        return agentRequestRepository.findByProcessStatusInOrderByCreatedTime(AgentRequestStatus.requested
                , AgentRequestStatus.forward);
    }

    @Override
    public AgentRequest newRequest(User user, String name, String mobile) throws RequestedException {
        if (user.getAgentInfo() != null)
            throw new IllegalStateException();
        if (agentRequestRepository.findByFromAndProcessStatusIn(user, AgentRequestStatus.requested
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

    @Override
    public AgentInfo makeAgent(User user) {
        if (user.getAgentInfo() != null)
            throw new IllegalStateException(user.getUsername() + "已经是合伙人了。");
        AgentInfo info = new AgentInfo();
        info.setJoinTime(LocalDateTime.now());
        user.setAgentInfo(info);

        user = userRepository.save(user);
        return user.getAgentInfo();
    }

    @Override
    public void declineRequest(User user, long id, String comment) {
        AgentRequest request = agentRequestRepository.getOne(id);

        request.setProcessStatus(AgentRequestStatus.reject);
        request.setComment(comment);
        agentRequestRepository.save(request);
    }

    @Override
    public void approveRequest(User user, long id, String comment) {
        AgentRequest request = agentRequestRepository.getOne(id);

        request.setProcessStatus(AgentRequestStatus.accept);
        request.setComment(comment);
        agentRequestRepository.save(request);

        makeAgent(request.getFrom());
    }
}
