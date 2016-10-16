package me.jiangcai.dating.service.impl;

import me.jiangcai.chanpay.Dictionary;
import me.jiangcai.chanpay.model.Bank;
import me.jiangcai.chanpay.model.SubBranch;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.PayResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class PayResourceServiceImpl implements PayResourceService {

    Pattern[] patterns = new Pattern[]{
            Pattern.compile("^.+公司(?!$)"),
            Pattern.compile("^.+银行(?!$)"),
            Pattern.compile("^.+合作社(?!$)"),
            Pattern.compile("^工行(?!$)"),
            Pattern.compile("^中国工商(?!$)"),
            Pattern.compile("^农行.+支行(?!$)"),
            Pattern.compile("^中国建行(?!$)"),
    };
    @Autowired
    private SubBranchBankRepository subBranchBankRepository;
    @Autowired
    private BankService bankService;
    @Autowired
    private Environment environment;

    @Transactional
    @PostConstruct
    public void init() {
        // 测试时间,每家银行 有2个支行即可
        // sql_dump生成者 需要全部添加
        // 其他时间跳过
        if (!environment.acceptsProfiles("test") || Dictionary.findAll(Bank.class).size() > bankService.list().size()) {
            // 在测试阶段只有量不够时才干这事儿
            for (Bank bank : Dictionary.findAll(Bank.class)) {
                bankService.updateBank(bank.getId(), bank.getName());
            }
        }
        Set<String> stopBanks = new HashSet<>();
        for (SubBranch branch : Dictionary.findAll(SubBranch.class)) {
            if (!environment.acceptsProfiles("sql_dump")) {
                // 正式的? 直接跳过
                if (!environment.acceptsProfiles("test"))
                    continue;
                if (stopBanks.contains(branch.getBankId()))
                    continue;
                if (subBranchBankRepository.countByBank_Code(branch.getBankId()) >= 2) {
                    stopBanks.add(branch.getBankId());
                    continue;
                }
            }// sql_dump 不会错过任何东西
            SubBranchBank branchBank = subBranchBankRepository.findOne(branch.getId());

            if (branchBank == null) {
                branchBank = new SubBranchBank();
                branchBank.setCode(branch.getId());
            }
            branchBank.setName(branch.getName());
            branchBank.setCityCode(branch.getCityId());
            branchBank.setBank(bankService.byCode(branch.getBankId()));
            subBranchBankRepository.save(branchBank);
        }
    }

//    private List<SubBranch> shortIt(List<SubBranch> collect) {
//        if (collect.size() == 1)
//            return collect;
//        try {
//            String head = collect.get(0).getName().substring(0, 1);
//            if (collect.stream()
//                    .filter(subBranch -> subBranch.getName().startsWith(head))
//                    .count()==collect.size())
//                return shortIt(collect, excepted+1);
//            else
//
//        }
//        return null;
//    }

    @Override
    public List<SubBranch> listSubBranches(String cityId, String bankId) {
        return (subBranchBankRepository.findByCityCodeAndBank_Code(cityId, bankId).stream()
                .map(toSubBranch())
                .collect(Collectors.toList()));
    }

    private Function<SubBranchBank, SubBranch> toSubBranch() {
        return branchBank -> {
            SubBranch subBranch = new SubBranch();
            subBranch.setCityId(branchBank.getCityCode());
            subBranch.setId(branchBank.getCode());
            subBranch.setName(branchBank.getName());

            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(subBranch.getName());
                if (matcher.find()) {
                    subBranch.setName(matcher.replaceFirst(""));
                    break;
                }
            }

            subBranch.setBankId(branchBank.getBank().getCode());

            return subBranch;
        };
    }
}
