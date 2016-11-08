package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.WealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

/**
 * @author CJ
 */
@Service("wealthService")
public class WealthServiceImpl implements WealthService {

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
