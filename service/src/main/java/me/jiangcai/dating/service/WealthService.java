package me.jiangcai.dating.service;

import me.jiangcai.dating.Locker;
import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 理财与借款服务
 *
 * @author CJ
 */
public interface WealthService {

    /**
     * 所有的合同
     */
    List<String> ContractElements = Collections.unmodifiableList(Arrays.asList(
            "CT001",
            "CT002",
//            "CT003",
            "CT004",
            "CT005",
            "CT006",
            "CT007",
            "CT008"
//            "CT009"
//            ,
//            "CT0010",
//            "CT0011",
//            "CT0012",
    ));

    /**
     * @return 是否支持更多理财选择
     */
    boolean moreFinancingSupport();

    Financing currentFinancing() throws IOException;

    URI financingUrl(User user, String financingId) throws IOException, VerifyCodeSentException;

    Loan[] loanList() throws IOException;

    /**
     * 借款申请,仅仅是开始一个申请
     *
     * @param openId         借款者
     * @param loan           相关产品
     * @param userDataId     {@link me.jiangcai.dating.entity.UserLoanData#id}如果提供了则其他数据都无需提供
     * @param amount         金额
     * @param name           真实姓名
     * @param number         身份证号码
     * @param address        地址
     * @param homeAddress    家庭住址
     * @param employer       工作单位
     * @param personalIncome 个人年收入（万）
     * @param familyIncome   家庭年收入(万)
     * @param age            年龄
     * @param hasHouse
     * @return 新建的申请
     */
    @Transactional
    ProjectLoanRequest loanRequest(String openId, ProjectLoan loan, Long userDataId, BigDecimal amount, String name
            , String number, Address address, String homeAddress, String employer, int personalIncome, int familyIncome
            , int age, boolean hasHouse);

    /**
     * 借款申请,仅仅是开始一个申请
     *
     * @param openId         借款者
     * @param loan           相关产品
     * @param amount         金额
     * @param period         周期（月）
     * @param userLoanDataId {@link me.jiangcai.dating.entity.UserLoanData#id}如果提供了则其他数据都无需提供
     * @param name           真实姓名
     * @param number         身份证号码
     * @param address        地址
     * @throws IOException
     */
    @Transactional
    LoanRequest loanRequest(String openId, Loan loan, BigDecimal amount, int period, Long userLoanDataId, String name
            , String number, Address address) throws IOException;

    /**
     * 提交一项借款申请
     *
     * @param loanRequestId 申请
     */
    @Transactional
    void submitLoanRequest(long loanRequestId);

    @Transactional(readOnly = true)
    List<LoanRequest> listLoanRequests(String openId);

    /**
     * 同意借款
     *
     * @param user      操作用户
     * @param requestId 请求id
     * @param comment   留言
     */
    @Transactional
    void approveLoanRequest(User user, long requestId, String comment) throws IOException;

    /**
     * 同意项目贷款
     *
     * @param user          操作用户
     * @param loanRequestId 请求id
     * @param amount        批准金额
     * @param termDays      批准期限
     * @param yearRate      批准年化利率
     * @param comment       留言
     */
    @Transactional
    void approveProjectLoanRequest(User user, long loanRequestId, BigDecimal amount, BigDecimal yearRate
            , int termDays, String comment) throws IOException;

    // 内部方法 请勿调用
    @ThreadSafe
    void approveProjectLoanRequestCore(Locker locker, User user, long loanRequestId, BigDecimal amount
            , BigDecimal yearRate, int termDays, String comment) throws IOException;

    /**
     * 拒绝借款
     *
     * @param user      操作用户
     * @param requestId 请求id
     * @param comment   留言
     */
    @Transactional
    void declineLoanRequest(User user, long requestId, String comment);

    // 内部方法 请勿调用
    @ThreadSafe
    void approveLoanRequestCore(Locker locker, User user, long requestId, String comment) throws IOException;

    /**
     * 更新借款申请的身份证复印件信息
     *
     * @param loanRequestId       借款id
     * @param backIdResourcePath  如不存在,请无视
     * @param frontIdResourcePath 如不存在,请无视
     * @param handResourcePath
     */
    @Transactional
    void updateLoanIDImages(long loanRequestId, String backIdResourcePath, String frontIdResourcePath, String handResourcePath) throws IOException;

    /**
     * 更新借款申请的卡号信息
     *
     * @param loanRequestId 借款id
     * @param cardId        如不存在,请无视
     */
    @Transactional
    void updateLoanCard(long loanRequestId, long cardId);

    /**
     * @return 下一个项目贷款期限周期, 单位:天
     */
    @Transactional(readOnly = true)
    int nextProjectLoanTerm();

    @Transactional
    void queryProjectLoanStatus(long id) throws IOException;

    @Transactional(readOnly = true)
    void sendNotify(long id);

    /**
     * 校验项目贷款验证码
     *
     * @param id               请求id
     * @param mobile           页面提交过来的手机号码，应该以请求信息里的为主
     * @param verificationCode 验证码
     * @throws IOException
     */
    void verifyProjectLoanCode(long id, String mobile, String verificationCode) throws IOException;

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
