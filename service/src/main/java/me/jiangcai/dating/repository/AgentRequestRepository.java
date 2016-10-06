package me.jiangcai.dating.repository;

import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.AgentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author CJ
 */
public interface AgentRequestRepository extends JpaRepository<AgentRequest, Long> {

    @Query("from AgentRequest r where r.processStatus in ?1")
    List<AgentRequest> findByProcessStatusInOrderByCreatedTime(AgentRequestStatus... statuses);

    @Query("from AgentRequest r where r.from = ?1 and r.processStatus in ?2")
    AgentRequest findByFromAndProcessStatusIn(User user, AgentRequestStatus... statuses);

}
