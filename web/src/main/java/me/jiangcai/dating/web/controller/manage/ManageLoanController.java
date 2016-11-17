package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.dating.service.WealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Loan_Value + "')")
@Controller
public class ManageLoanController {
    @Autowired
    private DataService dataService;
    @Autowired
    private WealthService wealthService;
    @Autowired
    private ConversionService conversionService;

    @RequestMapping(value = "/manage/loanRequest", method = RequestMethod.GET)
    public String index() {
        return "manage/loanRequest.html";
    }

    @RequestMapping(value = "/manage/data/loan", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
//    @Transactional
    public void change(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> data) throws IOException {
        String comment = (String) data.get("comment");
        @SuppressWarnings("unchecked") List<Number> targets = (List<Number>) data.get("targets");
        if ("approve".equals(data.get("type"))) {
            for (Number number : targets) {
                wealthService.approveLoanRequest(user, number.longValue(), comment);
            }
        } else {
            for (Number number : targets) {
                wealthService.declineLoanRequest(user, number.longValue(), comment);
            }
        }
    }

    @RequestMapping(value = "/manage/data/loan/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object all(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, LoanRequest.class, fieldList(), dataFilter(false));
    }

    @RequestMapping(value = "/manage/data/loan/pending", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object padding(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, LoanRequest.class, fieldList(), dataFilter(true));
    }

    /**
     * @param pendingOnly 是否只返回等待处理的
     * @return
     */
    private DataFilter<LoanRequest> dataFilter(boolean pendingOnly) {
        return (user, criteriaBuilder, root) -> {
            if (pendingOnly) {
                return root.get("processStatus").in(LoanRequestStatus.requested, LoanRequestStatus.forward);
            }
            return null;
        };
    }

    private List<DataField> fieldList() {
        return Arrays.asList(
                new DataService.NumberField("id", Long.class)
                , new DataService.StringField("headImageUrl") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).join("owner", JoinType.LEFT).get("headImageUrl");
                    }
                }, new DataService.StringField("userId") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).join("owner", JoinType.LEFT).get("id");
                    }
                }, new DataService.StringField("name") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).get("name");
                    }
                }, new DataService.StringField("city") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).get("address");
                    }
                }, new DataService.StringField("number") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).get("number");
                    }
                }, new DataService.StringField("projectName")
                , new DataService.NumberField("amount", Double.class) {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (origin == null)
                            return null;
                        BigDecimal decimal = (BigDecimal) origin;
                        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                , new DataService.NumberField("months", Integer.class)
                , new ToStringField("createdTime")
                , new DataService.EnumField("processStatus") {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (origin == null)
                            return null;
                        LoanRequestStatus processStatus = (LoanRequestStatus) origin;
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

    protected class ToStringField extends DataService.UnsearchableField {

        public ToStringField(String name) {
            super(name);
        }

        @Override
        public Object export(Object origin, MediaType type) {
            if (origin == null)
                return null;
            return conversionService.convert(origin, String.class);
//            return super.export(origin, type);
        }
    }
}
