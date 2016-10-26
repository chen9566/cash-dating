package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.repository.BankRepository;
import me.jiangcai.dating.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author CJ
 */
@Service
public class BankServiceImpl implements BankService {
    @Autowired
    private BankRepository bankRepository;

    @Override
    public Bank updateBank(String code, String name, String background, Boolean disabled) {
        Bank bank = bankRepository.findOne(code);
        if (bank == null) {
            bank = new Bank();
            bank.setCode(code);
        }
        if (name != null)
            bank.setName(name);
        if (background != null)
            bank.setBackground(background);
        if (disabled != null)
            bank.setDisabled(disabled);
        return bankRepository.save(bank);
    }

    @Override
    public List<Bank> list() {
        return bankRepository.findByDisabledFalse(new Sort(Sort.Direction.DESC, "weight"));
    }

    @Override
    public Bank byCode(String code) {
        return bankRepository.getOne(code);
    }

    @Override
    public Bank byName(String name) {
        return bankRepository.findByName(name);
    }
}
