package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Bank;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 银行服务
 * @author CJ
 */
public interface BankService {

    @Transactional
    Bank updateBank(String code,String name);

    @Transactional(readOnly = true)
    List<Bank> list();

    @Transactional(readOnly = true)
    Bank byCode(String code);
}
