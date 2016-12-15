package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.service.DataResourceField;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.WealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Project_Loan_Value + "')")
@Controller
public class ManageProjectLoanController extends AbstractLoanManage {
    @Autowired
    private DataService dataService;
    @Autowired
    private WealthService wealthService;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = "/manage/projectLoanRequestNextTerm", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public int nextProjectLoanTerm() {
        return wealthService.nextProjectLoanTerm();
    }

    @RequestMapping(value = "/manage/projectLoanRequest", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public String index(Model model) {
//        model.addAttribute("loanTermDays", wealthService.nextProjectLoanTerm());
//        model.addAttribute("yearRate", systemService.getProjectLoanYearRate());
        // 这里保存的是一个规格
        Map<Integer, BigDecimal> yearRates = new HashMap<>();
        final int[] terms = systemService.getProjectLoanTermsStyle();
        model.addAttribute("terms", terms);
        for (int term : terms) {
            yearRates.put(term, systemService.getProjectLoanYearRate(term));
        }
        model.addAttribute("yearRates", yearRates);

        return "manage/projectLoanRequest.html";
    }


    @RequestMapping(value = "/manage/data/projectLoan/sendNotify/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public void sendNotify(@PathVariable("id") long id) throws IOException {
        // 首先确保状态是 wait 的 不然就没有任意意义
        // 确认成功之后 就可以让他们更改状态了
        wealthService.sendNotify(id);
    }

    //检查投融家状态
    @RequestMapping(value = "/manage/data/projectLoan/query/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void queryStatus(@PathVariable("id") long id) throws IOException {
        // 首先确保状态是 wait 的 不然就没有任意意义
        // 确认成功之后 就可以让他们更改状态了
        wealthService.queryProjectLoanStatus(id);
    }

    @RequestMapping(value = "/manage/data/projectLoan", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void change(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> data) throws IOException {
        String comment = (String) data.get("comment");
        @SuppressWarnings("unchecked") List<Number> targets = (List<Number>) data.get("targets");
        if ("approve".equals(data.get("type"))) {
            // 这里应该有更多玩意儿
            System.out.println(data);
            BigDecimal amount = new BigDecimal((String) data.get("amount"));
            BigDecimal yearRate = new BigDecimal((String) data.get("yearRate"));
            int termDays = NumberUtils.parseNumber((String) data.get("termDays"), Integer.class);
            for (Number number : targets) {
                wealthService.approveProjectLoanRequest(user, number.longValue(), amount, yearRate, termDays, comment);
            }
        } else {
            for (Number number : targets) {
                wealthService.declineLoanRequest(user, number.longValue(), comment);
            }
        }
    }

    @RequestMapping(value = "/manage/data/projectLoan/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object all(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, ProjectLoanRequest.class, fieldList(), dataFilter(null));
    }

    @RequestMapping(value = "/manage/data/projectLoan/pending", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object padding(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, ProjectLoanRequest.class, fieldList(), dataFilter(LoanRequestStatus.requested));
    }

    @RequestMapping(value = "/manage/data/projectLoan/accepted", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object accepted(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, ProjectLoanRequest.class, fieldList(), dataFilter(LoanRequestStatus.accept));
    }

    @RequestMapping(value = "/manage/data/projectLoan/contract", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object contract(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, ProjectLoanRequest.class, fieldList()
                , (user1, criteriaBuilder, root)
                        -> criteriaBuilder.and(criteriaBuilder.equal(root.get("processStatus"), LoanRequestStatus.contract)
                        , criteriaBuilder.lessThan(criteriaBuilder.size(root.get("contracts")), WealthService.ContractElements.size())));
    }

    // 签章完毕
    @RequestMapping(value = "/manage/data/projectLoan/signed", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object signed(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, ProjectLoanRequest.class, fieldList()
                , (user1, criteriaBuilder, root)
                        -> criteriaBuilder.and(criteriaBuilder.equal(root.get("processStatus"), LoanRequestStatus.contract)
                        , criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(root.get("contracts")), WealthService.ContractElements.size())));
    }


    /**
     * @param status 要求现在的状态
     * @return
     */
    private DataFilter<ProjectLoanRequest> dataFilter(LoanRequestStatus status) {
        return (user, criteriaBuilder, root) -> {
            if (status != null)
                return criteriaBuilder.equal(root.get("processStatus"), status);
//
//            if (pendingOnly) {
//                return criteriaBuilder.and(type
//                        , root.get("processStatus").in(LoanRequestStatus.requested, LoanRequestStatus.forward));
//            }
            return criteriaBuilder.notEqual(root.get("processStatus"), LoanRequestStatus.init);
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
                }, new DataService.NumberField("applyAmount", Double.class) {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (origin == null)
                            return null;
                        BigDecimal decimal = (BigDecimal) origin;
                        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                , new DataService.NumberField("applyTermDays", Integer.class)
                , new DataService.NumberField("personalIncome", Integer.class) {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).get("personalIncome");
                    }
                }
                , new DataService.NumberField("familyIncome", Integer.class) {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).get("familyIncome");
                    }
                }
                , new DataService.NumberField("age", Integer.class) {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).get("age");
                    }
                }, new DataService.StringField("employer") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.join("loanData", JoinType.LEFT).get("employer");
                    }
                }
                , new ToStringField("createdTime")
                , new DataService.EnumField("processStatus") {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (origin == null)
                            return null;
                        LoanRequestStatus processStatus = (LoanRequestStatus) origin;
                        return processStatus.toHtml();
                    }
                }, new ToStringField("processTime")
                , new DataService.StringField("comment")
                , applicationContext.getBean(DataResourceField.class, "frontIDUrl"
                        , new Function<Root<?>, Expression<?>>() {
                            @Override
                            public Expression<?> apply(Root<?> root) {
                                return root.join("loanData", JoinType.LEFT).get("frontIdResource");
                            }
                        })
                , applicationContext.getBean(DataResourceField.class, "backIDUrl"
                        , new Function<Root<?>, Expression<?>>() {
                            @Override
                            public Expression<?> apply(Root<?> root) {
                                return root.join("loanData", JoinType.LEFT).get("backIdResource");
                            }
                        })
                , applicationContext.getBean(DataResourceField.class, "handIDUrl"
                        , new Function<Root<?>, Expression<?>>() {
                            @Override
                            public Expression<?> apply(Root<?> root) {
                                return root.join("loanData", JoinType.LEFT).get("handIdResource");
                            }
                        })
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
