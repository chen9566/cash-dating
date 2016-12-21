package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.dating.web.controller.support.DataController;
import me.jiangcai.wx.model.Gender;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@Controller
@RequestMapping(value = "/manage/data/user")
public class UserController extends DataController<User> {

    @Override
    protected Class<User> type() {
        return User.class;
    }

    @Override
    protected List<DataField> fieldList() {
        return Arrays.asList(
                new DataService.NumberField("id", Long.class)
                , new DataService.BooleanField("enabled")
                , new DataService.StringField("mobileNumber")
                , new DataService.StringField("guide") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("guideUser", JoinType.LEFT).get("nickname");
                    }
                }
                , new DataService.StringField("agent") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("agentUser", JoinType.LEFT).get("nickname");
                    }
                }
                , new DataService.StringField("nickname")
                , new DataService.StringField("headImageUrl")
                , new DataService.EnumField("gender") {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (origin == null)
                            return null;
                        Gender gender = (Gender) origin;
                        switch (gender) {
                            case male:
                                return "男";
                            case female:
                                return "女";
                            default:
                                return "未知";
                        }
                    }
                }
                , new DataService.StringField("city")
                , new DataService.EnumField("manageStatus") {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (origin == null)
                            return null;
                        return origin.toString();
                    }
                }
// , new DataService.NumberField("completedCashOrders", Long.class) {
//                    @Override
//                    public Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
//                        Subquery
//                        // Predicate type = criteriaBuilder.notEqual(root.type(), ProjectLoanRequest.class);
////                        Subquery<Long> userOrderSubQuery = query.subquery(Long.class);
////                        Root<CashOrder> userOrderRoot = userOrderSubQuery.from(CashOrder.class);
////                        userOrderSubQuery = userOrderSubQuery.where(builder.equal(userOrderRoot.get("owner"), root)
////                                , builder.isTrue(userOrderRoot.get("completed")));
//////                        userOrderSubQuery = userOrderSubQuery.groupBy(root);
////                        userOrderSubQuery = userOrderSubQuery.select(builder.count(userOrderRoot));
//////                        userOrderSubQuery.
////                        return userOrderSubQuery;
//                    }
//                }
        );
    }

    @Override
    protected DataFilter<User> dataFilter() {
        // 过滤掉所有非正式注册的用户 就是手机号码为空的
        return (user, criteriaBuilder, root) -> criteriaBuilder.and(
                criteriaBuilder.or(criteriaBuilder.notEqual(root.get("manageStatus"), ManageStatus.root),
                        criteriaBuilder.isNull(root.get("manageStatus")))
                , User.validUserPredicate(criteriaBuilder, root));
    }
}
