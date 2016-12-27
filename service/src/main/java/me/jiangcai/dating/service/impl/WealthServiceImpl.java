package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.Locker;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserLoanData;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.LoanStatus;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.repository.CardRepository;
import me.jiangcai.dating.repository.LoanRequestRepository;
import me.jiangcai.dating.service.CashStrings;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.WealthService;
import me.jiangcai.gaa.model.District;
import me.jiangcai.gaa.sdk.repository.DistrictRepository;
import me.jiangcai.lib.resource.Resource;
import me.jiangcai.lib.resource.service.ResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service("wealthService")
public class WealthServiceImpl implements WealthService {

    private static final Log log = LogFactory.getLog(WealthServiceImpl.class);

    @Autowired
    private TourongjiaService tourongjiaService;
    private Financing cache;
    private long lastCacheTime;
    private Loan[] loanCache;
    private long loanCacheTime;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LoanRequestRepository loanRequestRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private CashStrings cashStrings;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ResourceService resourceService;

    @Override
    public boolean moreFinancingSupport() {
        return false;
    }

    @Override
    public Financing currentFinancing() throws IOException {
        if (cache == null || System.currentTimeMillis() - lastCacheTime > 5 * 60 * 1000) {
            return reCache();
        }
        return cache;
    }

    @Override
    public URI financingUrl(User user, String financingId) throws IOException, VerifyCodeSentException {
        return tourongjiaService.financingURL(financingId, user.getMobileNumber());
    }

    @Override
    public Loan[] loanList() throws IOException {
        if (loanCache == null || System.currentTimeMillis() - loanCacheTime > 5 * 60 * 1000) {
            return reCacheLoan();
        }
        return loanCache;
    }

    @Override
    public ProjectLoanRequest loanRequest(String openId, ProjectLoan loan, Long userDataId, BigDecimal amount, String name
            , String number, Address address, String homeAddress, String employer, int personalIncome, int familyIncome
            , int age, boolean hasHouse) {
        UserLoanData userLoanData = updateUserLoanData(userDataId, openId, name, number, address, homeAddress, employer
                , personalIncome, familyIncome, age, hasHouse);

        ProjectLoanRequest request = new ProjectLoanRequest();
        request.setApplyAmount(amount);
        request.setApplyTermDays(nextProjectLoanTerm());
        request.setApplyCreditLimitYears(systemService.getProjectLoanCreditLimit());
        request.setTermDays(request.getApplyTermDays());
        request.setCreditLimitYears(request.getApplyCreditLimitYears());
        saveLoadRequest(loan, amount, 0, userLoanData, request);
        return loanRequestRepository.save(request);
    }

    @Override
    public LoanRequest loanRequest(String openId, Loan loan, BigDecimal amount, int period, Long userLoanDataId
            , String name
            , String number
            , Address address) throws IOException {
        UserLoanData userLoanData = updateUserLoanData(userLoanDataId, openId, name, number, address);

        LoanRequest request = new LoanRequest();
        saveLoadRequest(loan, amount, period, userLoanData, request);
        return loanRequestRepository.save(request);
    }

    private void saveLoadRequest(Loan loan, BigDecimal amount, int period, UserLoanData userLoanData, LoanRequest request) {
        request.setAmount(amount);
        request.setLoanData(userLoanData);
        request.setMonths(period);
        request.setProjectId(loan.getProductId());
        request.setProjectName(loan.getProductName());
        request.setCreatedTime(userLoanData.getLastUseTime());
        request.setProcessStatus(LoanRequestStatus.init);
    }

    private UserLoanData updateUserLoanData(Long userLoanDataId, String openId, String name, String number
            , Address address) {
        return updateUserLoanData(userLoanDataId, openId, name, number, address, null, null, 0, 0, 0, false);
    }

    private UserLoanData updateUserLoanData(Long userLoanDataId, String openId, String name, String number
            , Address address, String homeAddress, String employer, int personalIncome, int familyIncome
            , int age, boolean hasHouse) {
        User user = userService.byOpenId(openId);
        UserLoanData userLoanData;
        if (userLoanDataId != null) {
            userLoanData = user.getUserLoanDataList().stream()
                    .filter(userLoanData1 -> userLoanData1.getId().equals(userLoanDataId))
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new);
        } else {
            userLoanData = new UserLoanData();
            userLoanData.setName(name);
            userLoanData.setAddress(address);
            userLoanData.setNumber(number);
            userLoanData.setOwner(user);
            userLoanData.setCreatedTime(LocalDateTime.now());

            userLoanData.setHomeAddress(homeAddress);
            userLoanData.setEmployer(employer);
            userLoanData.setPersonalIncome(personalIncome);
            userLoanData.setFamilyIncome(familyIncome);
            userLoanData.setAge(age);
            userLoanData.setHasHouse(hasHouse);
            if (user.getUserLoanDataList() == null) {
                user.setUserLoanDataList(new HashSet<>());
            }
            user.getUserLoanDataList().add(userLoanData);
        }
        userLoanData.setLastUseTime(LocalDateTime.now());
        return userLoanData;
    }

    @Override
    public void submitLoanRequest(long loanRequestId) {
        LoanRequest loanRequest = loanRequestRepository.getOne(loanRequestId);
        if (loanRequest.getProcessStatus() == null || loanRequest.getProcessStatus() == LoanRequestStatus.init) {
            loanRequest.setProcessStatus(LoanRequestStatus.requested);
            loanRequestRepository.save(loanRequest);
        }
    }

    @Override
    public List<LoanRequest> listLoanRequests(String openId) {
        return loanRequestRepository.findByLoanData_Owner_OpenIdAndCompletedFalseOrderByCreatedTimeDesc(openId);
    }

    @Override
    public void approveLoanRequest(User user, long requestId, String comment) throws IOException {
        // 嘿嘿来了
        applicationContext.getBean(WealthService.class)
                .approveLoanRequestCore(("LoanRequest-" + requestId)::intern, user, requestId, comment);
    }

    @Override
    public void approveProjectLoanRequest(User user, long loanRequestId, BigDecimal amount, BigDecimal yearRate
            , int termDays, String comment) throws IOException {
        applicationContext.getBean(WealthService.class)
                .approveProjectLoanRequestCore(("LoanRequest-" + loanRequestId)::intern, user, loanRequestId, amount, yearRate, termDays, comment);
    }

    @Override
    public void declineLoanRequest(User user, long requestId, String comment) {
        LoanRequest request = loanRequestRepository.getOne(requestId);
        if (request.getProcessStatus() != LoanRequestStatus.requested
                && request.getProcessStatus() != LoanRequestStatus.forward)
            throw new IllegalStateException("订单状态已锁定。");
        request.setProcessStatus(LoanRequestStatus.reject);
        request.setProcessTime(LocalDateTime.now());
        request.setProcessor(user);
        request.setComment(comment);
        if (request instanceof ProjectLoanRequest) {
            applicationEventPublisher.publishEvent(((ProjectLoanRequest) request).toRejectNotification());
        }
    }

    @Override
    public void approveProjectLoanRequestCore(Locker locker, User user, long loanRequestId, BigDecimal amount
            , BigDecimal yearRate, int termDays, String comment) throws IOException {
        ProjectLoanRequest request = (ProjectLoanRequest) loanRequestRepository.getOne(loanRequestId);
        if (request.getSupplierRequestId() != null) {
            throw new IllegalStateException("already submitted:" + request);
        }
        if (request.getProcessStatus() != LoanRequestStatus.requested
                && request.getProcessStatus() != LoanRequestStatus.forward)
            throw new IllegalStateException("订单状态已锁定。");
        // 巴拉巴拉扒拉
        final District province = districtRepository.byChanpayCode(Locale.CHINA, request.getLoanData().getAddress().getProvince().getId());
        final District city = districtRepository.byChanpayCode(Locale.CHINA, request.getLoanData().getAddress().getCity().getId());
        String id = tourongjiaService.projectLoan(request.getLoanData().getOwner(), request.getLoanData().getName()
                , request.getLoanData().getNumber()
                , amount
                , termDays
                , request.getApplyCreditLimitYears()
                , province == null ? "" : province.getPostalCode()
                , city == null ? "" : city.getPostalCode()
                , request.getLoanData().getHomeAddress()
                , request.getLoanData().getFamilyIncome()
                , request.getLoanData().getPersonalIncome()
                , request.getLoanData().getAge()
                , request.getLoanData().isHasHouse()
                , new String[]{
                        resourceService.getResource(request.getLoanData().getFrontIdResource()).httpUrl().toString()
                        , resourceService.getResource(request.getLoanData().getBackIdResource()).httpUrl().toString()
                        , resourceService.getResource(request.getLoanData().getHandIdResource()).httpUrl().toString()
                }
        );

        request.setProcessStatus(LoanRequestStatus.accept);
        request.setProcessTime(LocalDateTime.now());
        request.setProcessor(user);
        request.setCreditLimitYears(request.getApplyCreditLimitYears());
        request.setAmount(amount);
        request.setYearRate(yearRate);
        request.setTermDays(termDays);
        request.setComment(comment);
        request.setSupplierRequestId(id);
    }

    @Override
    public void approveLoanRequestCore(Locker locker, User user, long requestId, String comment) throws IOException {
        LoanRequest request = loanRequestRepository.getOne(requestId);
        if (request.getSupplierRequestId() != null) {
            throw new IllegalStateException("already submitted:" + request);
        }
        if (request.getProcessStatus() != LoanRequestStatus.requested
                && request.getProcessStatus() != LoanRequestStatus.forward)
            throw new IllegalStateException("订单状态已锁定。");

        Loan loan = Stream.of(loanList())
                .filter(loan1 -> loan1.getProductId().equals(request.getProjectId()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        String term = Stream.of(loan.getTerm())
                .filter(s -> cashStrings.termInteger(s) == request.getMonths())
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        final District province = districtRepository.byChanpayCode(Locale.CHINA, request.getLoanData().getAddress().getProvince().getId());
        final District city = districtRepository.byChanpayCode(Locale.CHINA, request.getLoanData().getAddress().getCity().getId());
        String id = tourongjiaService.loan(loan, term, request.getLoanData().getOwner(), request.getLoanData().getName()
                , request.getAmount()
                , province == null ? "" : province.getPostalCode()
                , city == null ? "" : city.getPostalCode()
                , ""
        );
        request.setSupplierRequestId(id);
        request.setProcessStatus(LoanRequestStatus.accept);
        request.setProcessTime(LocalDateTime.now());
        request.setProcessor(user);
        request.setComment(comment);
    }

    @Override
    public void updateLoanIDImages(long loanRequestId, String backIdResourcePath, String frontIdResourcePath
            , String handResourcePath) throws IOException {
        // 从原资源体系删除
        LoanRequest loanRequest = loanRequestRepository.getOne(loanRequestId);
        if (backIdResourcePath != null) {
            loanRequest.getLoanData().setBackIdResource(fromTmp(backIdResourcePath));
        }
        if (frontIdResourcePath != null) {
            loanRequest.getLoanData().setFrontIdResource(fromTmp(frontIdResourcePath));
        }
        if (handResourcePath != null) {
            loanRequest.getLoanData().setHandIdResource(fromTmp(handResourcePath));
        }
    }

    private String fromTmp(String resourcePath) throws IOException {
        //名字就不改了
        Resource resource = resourceService.getResource(resourcePath);
        String name = "ids/" + resource.getFilename();
        try (InputStream data = resource.getInputStream()) {
            resourceService.uploadResource(name, data);
        }
        resourceService.deleteResource(resourcePath);
        return name;
    }

    @Override
    public void updateLoanCard(long loanRequestId, long cardId) {
//        cardRepository.delete(cardId);
    }

    @Override
    public int nextProjectLoanTerm() {
        // 所有通过的统计一下
        long count = loanRequestRepository.countByProcessStatus(LoanRequestStatus.contract);
        int[] terms = systemService.getProjectLoanTermsStyle();
        int[] counts = new int[terms.length];
        for (int i = 0; i < terms.length; i++) {
            counts[i] = systemService.getProjectLoanCountRate(terms[i]);
        }

        int last = (int) (count % sum(counts));
        for (int i = 0; i < terms.length; i++) {
            if (last < counts[i]) {
                return terms[i];
            }
            last -= counts[i];
        }

        return terms[terms.length - 1];
    }

    private long sum(int[] data) {
        long sum = 0;
        for (int x : data)
            sum = sum + x;
        return sum;
    }

    @Override
    public void queryProjectLoanStatus(long id) throws IOException {
        ProjectLoanRequest request = (ProjectLoanRequest) loanRequestRepository.getOne(id);
        if (request.getProcessStatus() != LoanRequestStatus.accept && StringUtils.isEmpty(request.getSupplierRequestId()))
            return;
        {
            LoanStatus loanStatus = tourongjiaService.checkLoanStatus(request.getSupplierRequestId());
            if (loanStatus == LoanStatus.success) {
                request.setProcessStatus(LoanRequestStatus.contract);
                supplierAcceptProjectLoanRequest(request);
            } else if (loanStatus == LoanStatus.failed) {
                request.setProcessStatus(LoanRequestStatus.reject);
                request.setComment("被投融家拒绝");
                supplierRejectProjectLoanRequest(request);
            }
        }
    }

    private void supplierRejectProjectLoanRequest(ProjectLoanRequest request) {
        log.info("[TRJ] reject loan:" + request.getId());
        applicationEventPublisher.publishEvent(request.toRejectNotification());
    }

    private void supplierAcceptProjectLoanRequest(ProjectLoanRequest request) {
        log.info("[TRJ] allow loan:" + request.getId());
        applicationEventPublisher.publishEvent(request.toAcceptNotification());
    }

    private void supplierFailedProjectLoanRequest(ProjectLoanRequest request) {
        log.fatal("Supplier Failed " + request.getId());
    }

    private void supplierSuccessProjectLoanRequest(ProjectLoanRequest request) {
        log.fatal("Supplier Success " + request.getId());
    }

    @Override
    public void sendNotify(long id) {
        ProjectLoanRequest request = (ProjectLoanRequest) loanRequestRepository.getOne(id);
        if (request.getProcessStatus() != LoanRequestStatus.contract)
            throw new IllegalStateException("Bad Status:" + request.getProcessStatus());
        //都弄好了就别bb了
//        if (request.getContracts().size() == ContractElements.size())
//            throw new IllegalStateException("all contracts has signed");
        applicationEventPublisher.publishEvent(request.toAcceptNotification());
    }

    private void supplierChangeStatus(String id, String comment, LoanRequestStatus targetStatus
            , Consumer<ProjectLoanRequest> projectLoanRequestConsumer) {
        LoanRequest request = loanRequestRepository.findBySupplierRequestId(id);

        if (request.getProcessStatus() == targetStatus)
            return;
        if (!StringUtils.isEmpty(comment))
            request.setComment(comment);
        request.setProcessStatus(targetStatus);
        if (request instanceof ProjectLoanRequest) {
            projectLoanRequestConsumer.accept((ProjectLoanRequest) request);
        }
    }

    @Override
    public void supplierRejectLoan(String id, String comment) {
        supplierChangeStatus(id, comment, LoanRequestStatus.reject, this::supplierRejectProjectLoanRequest);
    }

    @Override
    public void supplierAcceptLoan(String id, String comment) {
        supplierChangeStatus(id, comment, LoanRequestStatus.contract, this::supplierAcceptProjectLoanRequest);
    }

    @Override
    public void supplierFailedLoan(String id, String comment) {
        supplierChangeStatus(id, comment, LoanRequestStatus.failed, this::supplierFailedProjectLoanRequest);
    }

    @Override
    public void supplierSuccessLoan(String id, String comment) {
        supplierChangeStatus(id, comment, LoanRequestStatus.success, this::supplierSuccessProjectLoanRequest);
    }

    private Loan[] reCacheLoan() throws IOException {
        try {
            loanCache = tourongjiaService.loanList();
            loanCacheTime = System.currentTimeMillis();
        } catch (IOException ignore) {
            loanCache = tourongjiaService.loanList();
            loanCacheTime = System.currentTimeMillis();
        }
        // 添加 工程贷款
        Loan[] newArray = new Loan[loanCache.length + 1];
        newArray[0] = new ProjectLoan();
        System.arraycopy(loanCache, 0, newArray, 1, loanCache.length);
        loanCache = newArray;
        return loanCache;
    }

    private Financing reCache() throws IOException {
        try {
            cache = tourongjiaService.recommend();
            lastCacheTime = System.currentTimeMillis();
        } catch (IOException ignore) {
            cache = tourongjiaService.recommend();
            lastCacheTime = System.currentTimeMillis();
        }
        return cache;
    }
}
