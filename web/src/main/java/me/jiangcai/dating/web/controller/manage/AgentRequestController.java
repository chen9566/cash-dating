package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.AgentRequestStatus;
import me.jiangcai.dating.service.AgentService;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.dating.web.controller.support.DataController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Agent_Value + "')")
@Controller
@RequestMapping("/manage/data/agentRequest/pending")
public class AgentRequestController extends DataController<AgentRequest> {

    @Autowired
    private AgentService agentService;

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void change(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> data) {
        String comment = (String) data.get("comment");
        @SuppressWarnings("unchecked") List<Number> targets = (List<Number>) data.get("targets");
        if ("approve".equals(data.get("type"))) {
            for (Number number : targets) {
                agentService.approveRequest(user, number.longValue(), comment);
            }
        } else {
            for (Number number : targets) {
                agentService.declineRequest(user, number.longValue(), comment);
            }
        }
    }

    @Override
    protected Class<AgentRequest> type() {
        return AgentRequest.class;
    }

    @Override
    protected List<DataField> fieldList() {
        return Arrays.asList(
                new DataService.NumberField("id", Long.class)
                , new DataService.StringField("headImageUrl") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("from", JoinType.LEFT).get("headImageUrl");
                    }
                }, new DataService.StringField("city") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("from", JoinType.LEFT).get("city");
                    }
                }, new DataService.StringField("nickname") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("from", JoinType.LEFT).get("nickname");
                    }
                }, new DataService.StringField("name")
                , new DataService.StringField("mobileNumber")
                , new ToStringField("createdTime")
                , new DataService.EnumField("processStatus") {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (origin == null)
                            return null;
                        AgentRequestStatus processStatus = (AgentRequestStatus) origin;
                        switch (processStatus) {
                            case requested:
                                return "处理中";
                            case forward:
                                return "转发中";
                            case reject:
                                return "已被拒绝";
                            default:
                                return "已处理";
                        }
                    }
                }, new ToStringField("processTime")
                , new DataService.StringField("comment")
        );
    }

    @Override
    protected DataFilter<AgentRequest> dataFilter() {
        return new DataFilter<AgentRequest>() {
            @Override
            public Predicate dataFilter(User user, CriteriaBuilder criteriaBuilder, Root<AgentRequest> root) {
                final Path<Object> status = root.get("processStatus");
                return criteriaBuilder.or(
                        criteriaBuilder.isNull(status),
                        criteriaBuilder.equal(status, AgentRequestStatus.requested)
                        , criteriaBuilder.equal(status, AgentRequestStatus.forward)
                );
            }

            @Override
            public Order defaultOrder(CriteriaBuilder criteriaBuilder, Root<AgentRequest> root) {
                return criteriaBuilder.desc(root.get("createdTime"));
            }
        };
    }
}
