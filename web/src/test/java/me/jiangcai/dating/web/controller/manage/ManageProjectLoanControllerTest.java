package me.jiangcai.dating.web.controller.manage;

import com.jayway.jsonpath.JsonPath;
import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
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
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 错误的测试用例 也要做
 *
 * @author CJ
 */
@AsManage(ManageStatus.projectLoanSupplier)
public class ManageProjectLoanControllerTest extends ManageWebTest {

    @Autowired
    private WealthService wealthService;
    @Autowired
    private CashStrings cashStrings;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @Test
    public void index() {
        driver.get("http://localhost/manage/projectLoanRequest");
        assertThat(driver.getTitle())
                .isEqualTo("项目贷款");
//        System.out.println(driver.getPageSource());
    }

    @Test
    public void data() throws Exception {
        MockHttpSession session = mvcLogin();
        int currentAll = JsonPath.read(mockMvc.perform(getWeixin("/manage/data/projectLoan/all").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$.total");
        int currentPending = JsonPath.read(mockMvc.perform(getWeixin("/manage/data/projectLoan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$.total");
        int currentAccepted = JsonPath.read(mockMvc.perform(getWeixin("/manage/data/projectLoan/accepted").session(session)
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

        // 这是普通贷款跟我们无关的
        mockMvc.perform(getWeixin("/manage/data/projectLoan/all").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentAll));
        mockMvc.perform(getWeixin("/manage/data/projectLoan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentPending));

        // 我们新增一笔项目贷款
        wealthService.submitLoanRequest(newProjectLoanRequest(userOpenId).getId());
        wealthService.submitLoanRequest(newProjectLoanRequest(userOpenId).getId());

        mockMvc.perform(getWeixin("/manage/data/projectLoan/all").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/projectLoanRequest.json"));

        long requestId = firstPadding(session);
        declineLoan(session, requestId).andExpect(status().isOk());
        declineLoan(session, requestId)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().encoding("UTF-8"))
//                .andDo(print())
        ;
        approveLoan(session, requestId, "200000", "180", "0.1")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().encoding("UTF-8"))
//                .andDo(print())
        ;
        //第二次再拿  肯定不是了
        final long firstPaddingId = firstPadding(session);
        assertThat(firstPaddingId)
                .isNotEqualTo(requestId);
        assertThat(loanRequestRepository.getOne(firstPaddingId).getClass())
                .isEqualTo(ProjectLoanRequest.class);

        approveLoan(session, firstPaddingId, "200000", "180", "0.1").andExpect(status().isOk());
        declineLoan(session, firstPaddingId)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().encoding("UTF-8"))
//                .andDo(print())
        ;
        ;
        approveLoan(session, firstPaddingId, "200000", "180", "0.1")
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().encoding("UTF-8"))
//                .andDo(print())
        ;

        mockMvc.perform(getWeixin("/manage/data/projectLoan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentPending));

        mockMvc.perform(getWeixin("/manage/data/projectLoan/accepted").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentAccepted + 1));


    }


    private ResultActions declineLoan(MockHttpSession session, long requestId) throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("type", "decline");
        return changeLoan(data, session, requestId);
    }

    private ResultActions approveLoan(MockHttpSession session, long requestId, String amount, String term, String rate) throws Exception {
        HashMap<String, Object> data = new HashMap<>();
        data.put("type", "approve");
        data.put("amount", amount);
        data.put("termDays", term);
        data.put("yearRate", rate);
        return changeLoan(data, session, requestId);
    }

    private ResultActions changeLoan(Map<String, Object> data, MockHttpSession session, long requestId) throws Exception {
        data.put("targets", new Long[]{requestId});

        return mockMvc.perform(putWeixin("/manage/data/projectLoan").session(session)
                .header("X-Requested-With", "XMLHttpRequest")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(data)));
    }

    private long firstPadding(MockHttpSession session) throws Exception {
        return ((Number) JsonPath.read(mockMvc.perform(getWeixin("/manage/data/projectLoan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/projectLoanRequest.json"))
//                .andDo(print())
                .andReturn().getResponse().getContentAsString(), "$.rows[0].id")).longValue();
    }

    /**
     * 创建一个项目贷款完整订单
     *
     * @param openId
     * @return
     */
    private ProjectLoanRequest newProjectLoanRequest(String openId) throws IOException {
        Address address = new Address();
        address.setProvince(PayResourceService.listProvince().stream().max(new RandomComparator()).orElse(null));
        address.setCity(address.getProvince().getCityList().stream().max(new RandomComparator()).orElse(null));

        ProjectLoan projectLoan = new ProjectLoan();
        ProjectLoanRequest loanRequest = wealthService.loanRequest(openId, projectLoan, null
                , new BigDecimal(projectLoan.getMinAmount() + random.nextInt(projectLoan.getAmountInteger() - projectLoan.getMinAmount()))
                , "随意人", RandomStringUtils.randomNumeric(18), address, UUID.randomUUID().toString()
                , UUID.randomUUID().toString(), random.nextInt(100), random.nextInt(100), random.nextInt(100), random.nextBoolean());
        wealthService.updateLoanIDImages(loanRequest.getId(), randomImageResourcePath(), randomImageResourcePath(), randomImageResourcePath());
        return loanRequest;
    }

}