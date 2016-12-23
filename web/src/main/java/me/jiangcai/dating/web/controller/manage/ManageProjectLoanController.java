package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.selection.Report;
import me.jiangcai.dating.selection.SimpleSelection;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static me.jiangcai.dating.entity.support.LoanRequestStatus.init;
import static me.jiangcai.dating.entity.support.LoanRequestStatus.requested;

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
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

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
        return dataService.data(user, search, sort, order, offset, limit, ProjectLoanRequest.class, fieldList(), dataFilter(requested));
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
//                        , criteriaBuilder.lessThan(criteriaBuilder.size(root.get("contracts")), WealthService.ContractElements.size())
                        , ProjectLoanRequest.HasSignedPredicate(criteriaBuilder, root).not()
                ));
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
                        , ProjectLoanRequest.HasSignedPredicate(criteriaBuilder, root)
//                        , criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(root.get("contracts")), WealthService.ContractElements.size())
                ));
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
            return criteriaBuilder.notEqual(root.get("processStatus"), init);
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
                , new DataService.BooleanField("signed") {
                    @Override
                    public Object export(Object origin, MediaType type) {
                        ProjectLoanRequest request = (ProjectLoanRequest) origin;
                        return request.getContracts().size() >= WealthService.ContractElements.size();
                    }

                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root;
                    }
                }
        );
    }

    // 报表
    @RequestMapping(method = RequestMethod.GET, value = "/manage/export/projectLoan/index")
    public String exportIndex() {
        return "manage/projectLoanRequestReport.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/manage/exportTotal/projectLoan")
    @Transactional(readOnly = true)
    @ResponseBody
    public long exportTotal(@RequestParam(required = false) LocalDate startDate
            , @RequestParam(required = false) LocalDate endDate
            , Integer minAmount, Integer maxAmount, Integer term
            , String worker, Integer status, String comment) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<ProjectLoanRequest> root = criteriaQuery.from(ProjectLoanRequest.class);

        Predicate[] predicates = getPredicates(root, startDate, endDate, minAmount, maxAmount, term, worker, status
                , comment
                , builder);

        criteriaQuery = criteriaQuery.where(predicates);
        criteriaQuery = criteriaQuery.select(builder.count(root));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * @param startDate 闭合开始时间
     * @param endDate   闭合结束时间
     * @param minAmount 闭合最小值
     * @param maxAmount 闭合最大值
     * @param term      可选期限
     * @param worker    可选处理者姓名
     * @param status    0 待款爷审核 1 待投融家审核 2 待签章 3 完成
     * @param comment   可选备注
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/manage/export/projectLoan")
    @Transactional(readOnly = true)
    public Object export(@RequestParam(required = false) LocalDate startDate
            , @RequestParam(required = false) LocalDate endDate
            , Integer minAmount, Integer maxAmount, Integer term
            , String worker, Integer status, String comment) {
        // 用户申请时间 申请人姓名 申请人身份证号码 申请人电话 借款金额 产品期限 款爷审核时间 投融家审核时间 审核人 审核状态 备注
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<ProjectLoanRequest> criteriaQuery = builder.createQuery(ProjectLoanRequest.class);
        Root<ProjectLoanRequest> root = criteriaQuery.from(ProjectLoanRequest.class);

        Predicate[] predicates = getPredicates(root, startDate, endDate, minAmount, maxAmount, term, worker, status
                , comment
                , builder);

        criteriaQuery = criteriaQuery.where(predicates);

        TypedQuery<ProjectLoanRequest> typedQuery = entityManager.createQuery(criteriaQuery);

        // 用户申请时间 申请人姓名 申请人身份证号码 申请人电话 借款金额 产品期限 款爷审核时间 投融家审核时间 审核人 审核状态 备注

        return new Report<>("网商宝报表", typedQuery.getResultList(), Arrays.asList(
                new SimpleSelection<>("用户申请时间", LocalDateTime.class, ProjectLoanRequest::getCreatedTime)
                , new SimpleSelection<>("申请人姓名", String.class, request -> request.getLoanData().getName())
                , new SimpleSelection<>("申请人身份证号码", String.class, request -> request.getLoanData().getNumber())
                , new SimpleSelection<>("申请人电话", String.class, request -> request.getLoanData().getOwner().getMobileNumber())
                , new SimpleSelection<>("借款金额", BigDecimal.class, ProjectLoanRequest::getApplyAmount)
                , new SimpleSelection<>("产品期限", String.class, request -> request.getApplyTermDays() + "天")
                , new SimpleSelection<>("款爷审核时间", LocalDateTime.class, LoanRequest::getProcessTime)
                , new SimpleSelection<>("审核人", String.class, request -> request.getProcessor() == null ? "" : request.getProcessor().getNickname())
                , new SimpleSelection<>("审核状态", String.class, request -> {
                    switch (request.getProcessStatus()) {
                        case requested:
                            return "待款爷审核";
                        case accept:
                            return "待投融家审核";
                        case contract:
                            if (request.getContracts().size() >= WealthService.ContractElements.size())
                                return "完成";
                            return "待签章";
                    }
                    return "未知";
                })
                , new SimpleSelection<>("备注", String.class, LoanRequest::getComment)
        ));
    }

    private Predicate[] getPredicates(Root<ProjectLoanRequest> root, LocalDate startDate, LocalDate endDate, Integer minAmount, Integer maxAmount
            , Integer term, String worker, Integer status, String comment, CriteriaBuilder builder) {
        final ArrayList<Predicate> predicateArrayList = new ArrayList<>();

        Expression<LocalDateTime> createdTime = root.get("createdTime");

        if (startDate != null) {
            LocalDateTime startTime = LocalDateTime.now().with(startDate).withHour(0).withMinute(0).withSecond(0);
            predicateArrayList.add(builder.greaterThanOrEqualTo(createdTime, startTime));
        }
        if (endDate != null) {
            LocalDateTime endTime = LocalDateTime.now().with(endDate).withHour(23).withMinute(59).withSecond(59);
            predicateArrayList.add(builder.lessThanOrEqualTo(createdTime, endTime));
        }

        Expression<BigDecimal> amount = root.get("applyAmount");
        if (minAmount != null) {
            predicateArrayList.add(builder.greaterThanOrEqualTo(amount, BigDecimal.valueOf(minAmount.longValue())));
        }
        if (maxAmount != null) {
            predicateArrayList.add(builder.lessThanOrEqualTo(amount, BigDecimal.valueOf(maxAmount.longValue())));
        }

        //周期
        Expression applyTermDays = root.get("applyTermDays");
        if (term != null) {
            predicateArrayList.add(builder.equal(applyTermDays, applyTermDays));
        }

        //处理者
        if (!StringUtils.isEmpty(worker)) {
            Join<ProjectLoanRequest, User> processor = root.join("processor", JoinType.LEFT);
            predicateArrayList.add(builder.like(processor.get("nickname"), "%" + worker + "%"));
        }

        //状态
//        0 待款爷审核 1 待投融家审核 2 待签章 3 完成
        Expression processStatus = root.get("processStatus");
        if (status == null) {
            predicateArrayList.add(builder.notEqual(processStatus, LoanRequestStatus.init));
        } else {
            switch (status) {
                case 0:
                    predicateArrayList.add(builder.equal(processStatus, LoanRequestStatus.requested));
                    break;
                case 1:
                    predicateArrayList.add(builder.equal(processStatus, LoanRequestStatus.accept));
                    break;
                case 2:
                    predicateArrayList.add(builder.equal(processStatus, LoanRequestStatus.contract));
                    predicateArrayList.add(ProjectLoanRequest.HasSignedPredicate(builder, root).not());
                    break;
                case 3:
                    predicateArrayList.add(builder.equal(processStatus, LoanRequestStatus.contract));
                    predicateArrayList.add(ProjectLoanRequest.HasSignedPredicate(builder, root));
                    break;
                default:
                    predicateArrayList.add(builder.notEqual(processStatus, LoanRequestStatus.init));
            }
        }

        //备注
        Expression<String> expressionComment = root.get("comment");
        if (!StringUtils.isEmpty(comment)) {
            predicateArrayList.add(builder.like(expressionComment, "%" + comment + "%"));
        }

        return predicateArrayList.toArray(new Predicate[predicateArrayList.size()]);
    }

    // 导出报表

    protected class ToStringField extends DataService.UnsearchableField {

        public ToStringField(String name) {
            super(name, Object.class);
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
