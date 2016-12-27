package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserLoanData;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.repository.LoanRequestRepository;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.security.MessageDigest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class TRJNotifyControllerTest extends WebTest {

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Test
    public void test() throws Exception {
        User user = userService.byOpenId(createNewUser().getOpenId());
        UserLoanData loanData = new UserLoanData();
        loanData.setName(RandomStringUtils.randomAscii(6));
        loanData.setOwner(user);
        ProjectLoanRequest projectLoanRequest = new ProjectLoanRequest();
        projectLoanRequest.setLoanData(loanData);

        projectLoanRequest.setSupplierRequestId(randomMobile());
        projectLoanRequest = loanRequestRepository.saveAndFlush(projectLoanRequest);

//        MockMvcResultHandlers.print();

        mockMvc.perform(putNotify("/trj/notify/ItemLoan/" + projectLoanRequest.getSupplierRequestId() + "/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        assertThat(loanRequestRepository.getOne(projectLoanRequest.getId()).getProcessStatus())
                .isEqualByComparingTo(LoanRequestStatus.reject);

        mockMvc.perform(putNotify("/trj/notify/ItemLoan/" + projectLoanRequest.getSupplierRequestId() + "/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        assertThat(loanRequestRepository.getOne(projectLoanRequest.getId()).getProcessStatus())
                .isEqualByComparingTo(LoanRequestStatus.contract);

        mockMvc.perform(putNotify("/trj/notify/ItemLoan/" + projectLoanRequest.getSupplierRequestId() + "/failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        assertThat(loanRequestRepository.getOne(projectLoanRequest.getId()).getProcessStatus())
                .isEqualByComparingTo(LoanRequestStatus.failed);

        mockMvc.perform(putNotify("/trj/notify/ItemLoan/" + projectLoanRequest.getSupplierRequestId() + "/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        assertThat(loanRequestRepository.getOne(projectLoanRequest.getId()).getProcessStatus())
                .isEqualByComparingTo(LoanRequestStatus.success);


    }

    private MockHttpServletRequestBuilder putNotify(String url) throws Exception {
        String signed = Hex.encodeHexString(MessageDigest.getInstance("MD5").digest(("http://localhost" + url + "Einstein").getBytes("UTF-8")));
        return put(url).header("Kuanye_Auth", signed)
                .accept(MediaType.APPLICATION_JSON);
    }


}