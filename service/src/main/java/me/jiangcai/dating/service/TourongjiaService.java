package me.jiangcai.dating.service;

import me.jiangcai.dating.model.trj.Loan;

import java.io.IOException;

/**
 * 投融家相关服务
 *
 * @author CJ
 */
public interface TourongjiaService {

    Object recommend() throws IOException;

    Loan[] loanList() throws IOException;

}
