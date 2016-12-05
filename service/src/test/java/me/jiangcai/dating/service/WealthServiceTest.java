package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.support.Address;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * 建立一些临时的数据
 *
 * @author CJ
 */
public class WealthServiceTest extends ServiceBaseTest {

    @Autowired
    private WealthService wealthService;

    @Test
    public void flow() throws IOException {
        String openId = createNewUser().getOpenId();
        LoanRequest request = wealthService.loanRequest(openId, wealthService.loanList()[0], randomOrderAmount(), 0, null, "XXX"
                , RandomStringUtils.randomNumeric(18), new Address());
        wealthService.submitLoanRequest(request.getId());
    }

}