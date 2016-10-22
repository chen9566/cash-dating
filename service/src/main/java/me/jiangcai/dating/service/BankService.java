package me.jiangcai.dating.service;

import me.jiangcai.dating.entity.Bank;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 银行服务
 *
 * @author CJ
 */
public interface BankService {

    /**
     * 更新银行信息
     *
     * @param code       code
     * @param name       可选的name
     * @param background 可选的背景色
     * @return 新的银行
     */
    @Transactional
    Bank updateBank(String code, String name, String background);

    @Transactional(readOnly = true)
    List<Bank> list();

    @Transactional(readOnly = true)
    Bank byCode(String code);

    @Transactional(readOnly = true)
    Bank byName(String name);
}
