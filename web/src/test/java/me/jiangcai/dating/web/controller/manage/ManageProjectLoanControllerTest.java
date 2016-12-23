package me.jiangcai.dating.web.controller.manage;

import com.jayway.jsonpath.JsonPath;
import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.csv.CVSWriter;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.repository.LoanRequestRepository;
import me.jiangcai.dating.selection.Report;
import me.jiangcai.dating.service.CashStrings;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.WealthService;
import me.jiangcai.dating.test.TestReportHandler;
import me.jiangcai.dating.web.converter.LocalDateFormatter;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Autowired
    private TourongjiaService tourongjiaService;
    @Autowired
    private LocalDateFormatter localDateFormatter;

    @SuppressWarnings("unchecked")
    @Test
    public void export() throws Exception {

        System.out.println(localDateFormatter.print(LocalDate.now(), null));
        System.out.println(localDateFormatter.parse("2016-12-1", null));

        MockHttpSession session = mvcLogin();

        String userOpenId = createNewUser().getOpenId();
//        Loan loan = new ProjectLoan();
//        Address address = new Address();
//        address.setProvince(PayResourceService.listProvince().stream().max(new RandomComparator()).orElse(null));
//        address.setCity(address.getProvince().getCityList().stream().max(new RandomComparator()).orElse(null));

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
        )
                .andExpect(status().isOk());

        Report<ProjectLoanRequest> requestReport = TestReportHandler.lastReport;

        newProjectLoanRequest(userOpenId);

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
        )
                .andExpect(status().isOk());
        Report<ProjectLoanRequest> requestReport2 = TestReportHandler.lastReport;
        assertThat(requestReport2.getData().size())
                .isEqualTo(requestReport.getData().size());

        // 提交一个
        final Long workingLoanRequestId = newProjectLoanRequest(userOpenId).getId();
        wealthService.submitLoanRequest(workingLoanRequestId);

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
        )
                .andExpect(status().isOk());
        Report<ProjectLoanRequest> requestReport3 = TestReportHandler.lastReport;
        assertThat(requestReport3.getData())
                .hasSize(requestReport.getData().size() + 1);

        // input字段测试
        mockMvc.perform(getWeixin("/manage/export/projectLoan?startDate=&endDate=&minAmount=&maxAmount=&term=&worker=&status=&comment=")
                .session(session)
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(requestReport3.getData().size());

        LocalDate today = LocalDate.now();
        // 如果今天是startDay应该还是能看到这条记录的
        int todays = (int) requestReport3.getData().stream()
                .filter(projectLoanRequest -> projectLoanRequest.getCreatedTime().toLocalDate().isEqual(today))
                .count();
        int notTodays = requestReport3.getData().size() - todays;

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("startDate", localDateFormatter.print(today, null))
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(todays);
        // 如果是明天那么将是0
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("startDate", localDateFormatter.print(today.plusDays(1), null))
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData()).isEmpty();
        // 如果今天是endDay 那看到的也是todays 提前一天则看到了 total-todays
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("endDate", localDateFormatter.print(today, null))
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(todays);

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("endDate", localDateFormatter.print(today.minusDays(1), null))
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(notTodays);

        //
        int toTestMinAmount = requestReport3.getData().stream().max(new RandomComparator()).orElse(null).getApplyAmount().intValue();
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("minAmount", "" + toTestMinAmount)
        )
                .andExpect(status().isOk());
        Report<ProjectLoanRequest> afterMinAmount = TestReportHandler.lastReport;
        afterMinAmount.getData().stream()
                .forEach(projectLoanRequest
                        -> assertThat(projectLoanRequest.getApplyAmount()).isGreaterThanOrEqualTo(BigDecimal.valueOf(toTestMinAmount)));
        int toTestMaxAmount = requestReport3.getData().stream().max(new RandomComparator()).orElse(null).getApplyAmount().intValue();
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("maxAmount", "" + toTestMaxAmount)
        )
                .andExpect(status().isOk());
        Report<ProjectLoanRequest> afterMaxAmount = TestReportHandler.lastReport;
        afterMaxAmount.getData().stream()
                .forEach(projectLoanRequest
                        -> assertThat(projectLoanRequest.getApplyAmount()).isLessThanOrEqualTo(BigDecimal.valueOf(toTestMaxAmount)));

        // 周期  随便找一个周期 然后算一下同类型的有多少个
        int theTerm = requestReport3.getData().stream()
                .max(new RandomComparator()).orElse(null).getApplyTermDays();
        int withTheTerms = (int) requestReport3.getData().stream()
                .filter(projectLoanRequest -> projectLoanRequest.getApplyTermDays() == theTerm)
                .count();
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("term", "" + theTerm)
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(withTheTerms);

        // 处理者
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("worker", currentUser().getNickname())
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .isEmpty();

        // 状态 好了以后 再跑一次处理者
//        0 待款爷审核 1 待投融家审核 2 待签章 3 完成
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "0")
        )
                .andExpect(status().isOk());
        int waitingWe = TestReportHandler.lastReport.getData().size();
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "1")
        )
                .andExpect(status().isOk());
        int waitingTRJ = TestReportHandler.lastReport.getData().size();
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "2")
        )
                .andExpect(status().isOk());
        int waitingSign = TestReportHandler.lastReport.getData().size();
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "3")
        )
                .andExpect(status().isOk());
        int dones = TestReportHandler.lastReport.getData().size();
        ProjectLoanRequest workingLoanRequest = (ProjectLoanRequest) loanRequestRepository.getOne(workingLoanRequestId);
        final String comment = UUID.randomUUID().toString();
        wealthService.approveProjectLoanRequest(currentUser(), workingLoanRequestId, workingLoanRequest.getApplyAmount()
                , workingLoanRequest.getYearRate(), workingLoanRequest.getApplyTermDays(), comment);

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "0")
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(waitingWe - 1);

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "1")
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(waitingTRJ + 1);

        makeLoanStatusTo(workingLoanRequestId, true);

        wealthService.queryProjectLoanStatus(workingLoanRequestId);

        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "1")
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(waitingTRJ);
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "2")
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(waitingSign + 1);
        // 完成签名
        // 这里插一句处理者
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("worker", currentUser().getNickname())
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(1);
        // 继续签名
        signAllContract(workingLoanRequestId);
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "2")
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(waitingSign);
        mockMvc.perform(getWeixin("/manage/export/projectLoan")
                .session(session)
                .param("status", "3")
        )
                .andExpect(status().isOk());
        assertThat(TestReportHandler.lastReport.getData())
                .hasSize(dones + 1);
        // 备注
        Report report = TestReportHandler.lastReport;
        CVSWriter writer = new CVSWriter();
        File file = new File("target/" + report.getName() + "." + writer.extension());
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            writer.writeTo(report, outputStream);
            outputStream.flush();
        }
    }

    @Test
    public void index() {
        driver.get("http://localhost/manage/projectLoanRequest");
        assertThat(driver.getTitle())
                .isEqualTo("项目贷款");
//        System.out.println(driver.getPageSource());
        driver.get("http://localhost/manage/export/projectLoan/index");
        assertThat(driver.getTitle())
                .isEqualTo("网商宝报表");

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
        int currentContract = JsonPath.read(mockMvc.perform(getWeixin("/manage/data/projectLoan/contract").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$.total");
        int currentSigned = JsonPath.read(mockMvc.perform(getWeixin("/manage/data/projectLoan/signed").session(session)
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

        wealthService.submitLoanRequest(wealthService.loanRequest(userOpenId, loan, new BigDecimal("20000")
                , cashStrings.termInteger(term), null
                , "摆渡人", RandomStringUtils.randomNumeric(18), address).getId());
        wealthService.submitLoanRequest(wealthService.loanRequest(userOpenId, loan, new BigDecimal("20000")
                , cashStrings.termInteger(term), null
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

        //先告诉系统应该允许通过
        makeLoanStatusTo(firstPaddingId, true);

        // 此处执行一个接口 检查
        mockMvc.perform(putWeixin("/manage/data/projectLoan/query/" + firstPaddingId).session(session))
                .andExpect(status().isOk());
        // 这个时候
        mockMvc.perform(getWeixin("/manage/data/projectLoan/accepted").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentAccepted));

        //
        mockMvc.perform(getWeixin("/manage/data/projectLoan/contract").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentContract + 1));
        // 我给填满8份合同
        signAllContract(firstPaddingId);

        mockMvc.perform(getWeixin("/manage/data/projectLoan/contract").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentContract));
        mockMvc.perform(getWeixin("/manage/data/projectLoan/signed").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(currentSigned + 1));

        ProjectLoanRequest projectLoanRequest = (ProjectLoanRequest) loanRequestRepository.getOne(firstPaddingId);
        assertThat(projectLoanRequest.getContracts())
                .hasSize(WealthService.ContractElements.size());

        // 手动通知
        mockMvc.perform(putWeixin("/manage/data/projectLoan/sendNotify/" + firstPaddingId).session(session))
                .andExpect(status().isOk());

    }

    private void makeLoanStatusTo(long requestId, boolean success) throws IOException {
        String id = loanRequestRepository.getOne(requestId).getSupplierRequestId();
        tourongjiaService.testMakeLoanStatus(id, success);
    }

    /**
     * 签订所有的合同
     *
     * @param id
     */
    private void signAllContract(long id) {
        ProjectLoanRequest request = (ProjectLoanRequest) loanRequestRepository.getOne(id);
        WealthService.ContractElements.forEach(type -> {
            request.getContracts().put(type, "");
        });
        loanRequestRepository.save(request);
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
        final String contentAsString = mockMvc.perform(getWeixin("/manage/data/projectLoan/pending").session(session)
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/projectLoanRequest.json"))
//                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        // 几个图必须是不同的地址的 而且必须都存在
        String front = JsonPath.read(contentAsString, "$.rows[0].frontIDUrl");
        String back = JsonPath.read(contentAsString, "$.rows[0].backIDUrl");
        String hand = JsonPath.read(contentAsString, "$.rows[0].handIDUrl");
        System.out.println(front);
        System.out.println(back);
        System.out.println(hand);
        assertThat(front).isNotEmpty()
                .isNotEqualTo(back)
                .isNotEqualTo(hand);
        assertThat(back).isNotEmpty()
                .isNotEqualTo(hand);
        assertThat(hand).isNotEmpty();


        return ((Number) JsonPath.read(contentAsString, "$.rows[0].id")).longValue();
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