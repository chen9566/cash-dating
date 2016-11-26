package me.jiangcai.dating.web.controller.manage;

import com.jayway.jsonpath.JsonPath;
import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.service.CashStrings;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.WealthService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.financial)
public class ManageLoanControllerTest extends ManageWebTest {

    @Autowired
    private WealthService wealthService;
    @Autowired
    private CashStrings cashStrings;

    @Test
    public void index() {
        BigDecimal bigDecimal = new BigDecimal("100");
        System.out.println(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));
        System.out.println(bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP));
        bigDecimal = new BigDecimal("200.555");
        System.out.println(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));
        System.out.println(bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP));
        driver.get("http://localhost/manage/loanRequest");
        assertThat(driver.getTitle())
                .isEqualTo("审批借款");
    }

    @Test
    public void data() throws Exception {
        MockHttpSession session = mvcLogin();
        int current = JsonPath.read(mockMvc.perform(getWeixin("/manage/data/loan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$.total");

        String userOpenId = createNewUser().getOpenId();
        Loan loan = Stream.of(wealthService.loanList())
                .max(new RandomComparator())
                .orElse(null);
        String term = loan.getTerm()[random.nextInt(loan.getTerm().length)];
        Address address = new Address();
        address.setProvince(PayResourceService.listProvince().stream().max(new RandomComparator()).orElse(null));
        address.setCity(address.getProvince().getCityList().stream().max(new RandomComparator()).orElse(null));

        // 这个只是玩玩 不会被检查出来的
        wealthService.loanRequest(userOpenId, loan, new BigDecimal("20000"), cashStrings.termInteger(term), null
                , "摆渡人", RandomStringUtils.randomNumeric(18), address);

        wealthService.submitLoanRequest(wealthService.loanRequest(userOpenId, loan, new BigDecimal("20000"), cashStrings.termInteger(term), null
                , "摆渡人", RandomStringUtils.randomNumeric(18), address));
        wealthService.submitLoanRequest(wealthService.loanRequest(userOpenId, loan, new BigDecimal("20000"), cashStrings.termInteger(term), null
                , "摆渡人", RandomStringUtils.randomNumeric(18), address));

        mockMvc.perform(getWeixin("/manage/data/loan/all").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/loanRequest.json"));

        long requestId = firstPadding(session);
        declineLoan(session, requestId);
        //第二次再拿  肯定不是了
        final long firstPaddingId = firstPadding(session);
        assertThat(firstPaddingId)
                .isNotEqualTo(requestId);
        approveLoan(session, firstPaddingId);

        mockMvc.perform(getWeixin("/manage/data/loan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(current));
    }

    private void declineLoan(MockHttpSession session, long requestId) throws Exception {
        changeLoan(session, requestId, "decline");
    }

    private void approveLoan(MockHttpSession session, long requestId) throws Exception {
        changeLoan(session, requestId, "approve");
    }

    private void changeLoan(MockHttpSession session, long requestId, String type) throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("targets", new Long[]{requestId});
        data.put("type", type);

        mockMvc.perform(putWeixin("/manage/data/loan").session(session)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(data)))
                .andExpect(status().isOk());
    }

    private long firstPadding(MockHttpSession session) throws Exception {
        return ((Number) JsonPath.read(mockMvc.perform(getWeixin("/manage/data/loan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/loanRequest.json"))
//                .andDo(print())
                .andReturn().getResponse().getContentAsString(), "$.rows[0].id")).longValue();
    }

}