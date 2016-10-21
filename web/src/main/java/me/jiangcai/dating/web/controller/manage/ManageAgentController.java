package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.dating.web.controller.support.DataController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Agent_Value + "')")
@RequestMapping(value = {"/manage/data/agent"})
@Controller
public class ManageAgentController extends DataController<User> {
    @Override
    protected Class<User> type() {
        return User.class;
    }

    @Override
    public Predicate dataFilter(User user, CriteriaBuilder criteriaBuilder, Root<User> root) {
        return criteriaBuilder.isNotNull(root.get("agentInfo"));
    }

    @Override
    protected List<DataField> fieldList() {
        return Arrays.asList(
                new DataService.NumberField("id", Long.class)
                , new DataService.StringField("city")
                , new DataService.StringField("nickname")
                , new DataService.StringField("mobileNumber")
                , new ToStringField("joinTime") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.get("agentInfo").get("joinTime");
//                        return super.selectExpression(root);
                    }
                }
        );
    }
}
