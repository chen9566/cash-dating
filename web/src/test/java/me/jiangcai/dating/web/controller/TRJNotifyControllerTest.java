package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.repository.LoanRequestRepository;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
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

        mockMvc.perform(putNotify("/trj/notify/ItemLoan/xoo/reject"))
                .andExpect(status().isNoContent());

        ProjectLoanRequest projectLoanRequest = newProjectLoanRequest(createNewUser().getOpenId());

        projectLoanRequest.setSupplierRequestId(randomMobile());
        projectLoanRequest = loanRequestRepository.save(projectLoanRequest);

//        MockMvcResultHandlers.print();

        mockMvc.perform(putNotify("/trj/notify/ItemLoan/" + projectLoanRequest.getSupplierRequestId() + "/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        assertThat(loanRequestRepository.getOne(projectLoanRequest.getId()).getProcessStatus())
                .isEqualByComparingTo(LoanRequestStatus.reject);

        // 1.0.4 之后 更改了 accept 接受的content格式
        mockMvc.perform(putNotify("/trj/notify/ItemLoan/" + projectLoanRequest.getSupplierRequestId() + "/accept"))
                .andExpect(status().isUnsupportedMediaType());

        String chineseComment = "中国" + randomEmailAddress();
        BigDecimal targetAmount = randomOrderAmount();
        mockMvc.perform(putNotify("/trj/notify/ItemLoan/" + projectLoanRequest.getSupplierRequestId() + "/accept")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"comment\":\"" + chineseComment + "\",\"amount\":" + targetAmount.toString() + "}")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
        assertThat(loanRequestRepository.getOne(projectLoanRequest.getId()).getComment())
                .isEqualTo(chineseComment);
        assertThat(loanRequestRepository.getOne(projectLoanRequest.getId()).getAmount())
                .isEqualTo(targetAmount);
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
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.TEXT_PLAIN)
                .content("make fun.");
    }


}