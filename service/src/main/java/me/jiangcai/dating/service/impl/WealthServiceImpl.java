package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserLoanData;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.repository.LoanRequestRepository;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.WealthService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

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
    public LoanRequest loanRequest(String openId, Loan loan, BigDecimal amount, int period, Long userLoanDataId
            , String name
            , String number
            , Address address) throws IOException {
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
            if (user.getUserLoanDataList() == null) {
                user.setUserLoanDataList(new HashSet<>());
            }
            user.getUserLoanDataList().add(userLoanData);
        }
        userLoanData.setLastUseTime(LocalDateTime.now());

        LoanRequest request = new LoanRequest();
        request.setAmount(amount);
        request.setLoanData(userLoanData);
        request.setMonths(period);
        request.setProjectId(loan.getProductId());
        request.setProjectName(loan.getProductName());
        request.setCreatedTime(userLoanData.getLastUseTime());
        // TODO 就差跟tourongjia push
        return loanRequestRepository.save(request);
    }

    @Override
    public List<LoanRequest> listLoanRequests(String openId) {
        return loanRequestRepository.findByLoanData_Owner_OpenIdAndCompletedFalseOrderByCreatedTimeDesc(openId);
    }

    private Loan[] reCacheLoan() throws IOException {
        try {
            loanCache = tourongjiaService.loanList();
            loanCacheTime = System.currentTimeMillis();
        } catch (IOException ignore) {
            loanCache = tourongjiaService.loanList();
            loanCacheTime = System.currentTimeMillis();
        }
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
