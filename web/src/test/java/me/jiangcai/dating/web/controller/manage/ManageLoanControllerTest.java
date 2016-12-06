package me.jiangcai.dating.web.controller.manage;

import com.jayway.jsonpath.JsonPath;
import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.repository.LoanRequestRepository;
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
import java.util.UUID;
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
    @SuppressWarnings("unused")
    @Autowired
    private LoanRequestRepository loanRequestRepository;

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
                , "摆渡人", RandomStringUtils.randomNumeric(18), address).getId());
        wealthService.submitLoanRequest(wealthService.loanRequest(userOpenId, loan, new BigDecimal("20000"), cashStrings.termInteger(term), null
                , "摆渡人", RandomStringUtils.randomNumeric(18), address).getId());

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
        assertThat(loanRequestRepository.getOne(firstPaddingId).getClass())
                .isEqualTo(LoanRequest.class);
        approveLoan(session, firstPaddingId);

        mockMvc.perform(getWeixin("/manage/data/loan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(current));
        // 必须确保 项目贷款 它是看不到的
        ProjectLoanRequest loanRequest = wealthService.loanRequest(userOpenId, new ProjectLoan(), null, new BigDecimal("170000"), "摆渡人"
                , RandomStringUtils.randomNumeric(18), address, UUID.randomUUID().toString()
                , UUID.randomUUID().toString(), 10, 10, 30);
        wealthService.submitLoanRequest(loanRequest.getId());

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