package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.WealthService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

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
    public void loanRequest(String openId, Loan loan, BigDecimal amount, int period, String name, String number
            , Address address) throws IOException {
        log.debug(address);
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
