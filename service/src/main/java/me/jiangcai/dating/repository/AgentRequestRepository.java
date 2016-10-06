package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.AgentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author CJ
 */
public interface AgentRequestRepository extends JpaRepository<AgentRequest, Long> {

    List<AgentRequest> findByProcessStatusOrderByCreatedTime(AgentRequestStatus... statuses);

    AgentRequest findByFromAndProcessStatus(User user, AgentRequestStatus... statuses);

}
