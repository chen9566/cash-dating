package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.MobileToken;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

/**
 * 投融家相关服务
 *
 * @author CJ
 */
public interface TourongjiaService {

    void sendCode(MobileToken token) throws IOException;

    void bind(String mobile, String code) throws IOException;

    Financing randomFinancing() throws IOException;

    Financing recommend() throws IOException;

    /**
     * @return 借款产品列表
     * @throws IOException
     */
    Loan[] loanList() throws IOException;

    /**
     * 申请借款
     *
     * @param loan     产品
     * @param term     周期{@link Loan#term}其中之一
     * @param user     用户
     * @param name     真实姓名
     * @param amount   金额
     * @param province 省id
     * @param city     市id
     * @param address  地址       @return 申请id
     * @throws IOException
     */
    String loan(Loan loan, String term, User user, String name, BigDecimal amount, String province, String city, String address)
            throws IOException;

    /**
     * 申请状态查询
     *
     * @param id 申请id
     * @return
     * @throws IOException
     */
    String checkLoanStatus(String id) throws IOException;

    MobileToken token(String mobile) throws IOException;

    /**
     * @param mobile
     * @return 登录页
     */
    URI loginURL(String mobile);

    /**
     * 选择了理财产品以后的地址
     *
     * @param financing 产品
     * @param mobile    手机号码
     * @return 具体URL
     * @throws IOException
     * @throws VerifyCodeSentException 如果未绑定,并且发送了验证码
     */
    URI financingURL(Financing financing, String mobile) throws IOException, VerifyCodeSentException;
}
