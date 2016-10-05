package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.support.AgentRequestStatus;
import me.jiangcai.dating.repository.AgentRequestRepository;
import me.jiangcai.dating.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
