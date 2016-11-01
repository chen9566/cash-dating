package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 投融家相关服务
 *
 * @author CJ
 */
public interface TourongjiaService {

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
}
