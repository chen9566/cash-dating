package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

/**
 * 理财与借款服务
 *
 * @author CJ
 */
public interface WealthService {

    /**
     * @return 是否支持更多理财选择
     */
    boolean moreFinancingSupport();

    Financing currentFinancing() throws IOException;

    URI financingUrl(User user, String financingId) throws IOException, VerifyCodeSentException;

    Loan[] loanList() throws IOException;

    /**
     * 借款申请
     *
     * @param openId         借款者
     * @param loan           相关产品
     * @param amount         金额
     * @param period         周期（月）
     * @param userLoanDataId {@link me.jiangcai.dating.entity.UserLoanData#id}如果提供了则其他数据都无需提供
     * @param name           真实姓名
     * @param number         身份证号码
     * @param address        地址    @throws IOException
     */
    @Transactional
    LoanRequest loanRequest(String openId, Loan loan, BigDecimal amount, int period, Long userLoanDataId, String name
            , String number, Address address) throws IOException;

    @Transactional(readOnly = true)
    List<LoanRequest> listLoanRequests(String openId);
//
//    /**
//     * @return 总投资金额
//     */
//    BigDecimal totalFinancingInvestment();
//
//    /**
//     * @return 总投资收益
//     */
//    BigDecimal totalFinancingProfit();
//
//    /**
//     * 设定虚假的总投资金额
//     *
//     * @param number 金额
//     */
//    void changeFakeTotalFinancingInvestment(BigDecimal number);
//
//    /**
//     * 设定虚假的总投资收益
//     *
//     * @param number 收益
//     */
//    void changeFakeTotalFinancingProfit(BigDecimal number);

}
