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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
    public LoanRequest loanRequest(String openId, ProjectLoan loan, Long userDataId, BigDecimal amount, String name
            , String number, Address address, String homeAddress, String employer, int personalIncome, int familyIncome
            , int age) {
        UserLoanData userLoanData = updateUserLoanData(userDataId, openId, name, number, address, homeAddress, employer
                , personalIncome, familyIncome, age);

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
        return updateUserLoanData(userLoanDataId, openId, name, number, address, null, null, 0, 0, 0);
    }

    private UserLoanData updateUserLoanData(Long userLoanDataId, String openId, String name, String number
            , Address address, String homeAddress, String employer, int personalIncome, int familyIncome
            , int age) {
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
    public void declineLoanRequest(User user, long requestId, String comment) {
        LoanRequest request = loanRequestRepository.getOne(requestId);
        request.setProcessStatus(LoanRequestStatus.reject);
        request.setProcessTime(LocalDateTime.now());
        request.setProcessor(user);
        request.setComment(comment);
    }

    @Override
    public void approveLoanRequestCore(Locker locker, User user, long requestId, String comment) throws IOException {
        LoanRequest request = loanRequestRepository.getOne(requestId);
        if (request.getSupplierRequestId() != null) {
            throw new IllegalStateException("already submitted:" + request);
        }
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
        return Integer.parseInt(new ProjectLoan().getTerm()[0]);
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
