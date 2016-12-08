package me.jiangcai.dating.web.controller.wealth;

import me.jiangcai.chanpay.model.City;
import me.jiangcai.chanpay.model.Province;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.page.FinancingPage;
import me.jiangcai.dating.page.LoanAmountPage;
import me.jiangcai.dating.page.LoanCompletedPage;
import me.jiangcai.dating.page.LoanHandIDPage;
import me.jiangcai.dating.page.LoanIDPage;
import me.jiangcai.dating.page.LoanPage;
import me.jiangcai.dating.page.LoanSubmitPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.repository.LoanRequestRepository;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.WealthService;
import me.jiangcai.gaa.sdk.repository.DistrictRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class WealthControllerTest extends LoginWebTest {

    @Autowired
    private WealthService wealthService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LoanRequestRepository loanRequestRepository;

    /**
     * 需要关注的数据是
     * 年龄,个人年收入,家庭年收入,有无房产
     *
     * @throws IOException
     */
    @Test
//    @Repeat(3)
    public void projectLoan() throws IOException {
        MyPage myPage = myPage();
        Loan[] loanList = wealthService.loanList();
//        System.out.println(Arrays.toString(loanList));

        LoanPage loanPage = myPage.toLoanPage();
        loanPage.assertList(loanList);

        // 选择了一款普通产品
        ProjectLoan loan = Stream.of(loanList).filter(loan1 -> loan1 instanceof ProjectLoan)
                .map(loan1 -> (ProjectLoan) loan1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到项目贷款"));

        LoanAmountPage page = loanPage.choose(loan.getProductName());

//        page.checkAgreement();
        page.assertLoan(loan, wealthService.nextProjectLoanTerm());
        // 随机从50000-max
        int amount = 1 + random.nextInt((loan.getAmountInteger()));
//        String term = loan.getTerm()[random.nextInt(loan.getTerm().length)];
        LoanSubmitPage submitPage = page.loan(amount, null);

        //填入姓名,身份证号码, 随便弄一个地址
        String name = RandomStringUtils.randomAscii(2 + random.nextInt(3));
        String number = RandomStringUtils.randomNumeric(18);

        Province province = null;
        while (province == null || districtRepository.byChanpayCode(Locale.CHINA, province.getId()) == null) {
            province = PayResourceService.listProvince().stream().max(new RandomComparator()).orElseThrow(IllegalStateException::new);
        }
        City city = null;
        while (city == null || districtRepository.byChanpayCode(Locale.CHINA, city.getId()) == null) {
            city = province.getCityList().stream().max(new RandomComparator()).orElseThrow(IllegalStateException::new);
        }

        int age = 18 + random.nextInt(40);
        boolean hasHouse = random.nextBoolean();
        int familyIncome = 1 + random.nextInt(100);
        int personalIncome = 1 + random.nextInt(100);

        // 年龄,个人年收入,家庭年收入,有无房产
        // 原数据也需要保留
        LoanIDPage idPage = submitPage.submit(name, number, province.getName(), city.getName(), hasHouse, age
                , familyIncome, personalIncome);

        LoanHandIDPage handIDPage = idPage.next(randomImageResourcePath(), randomImageResourcePath());

        LoanCompletedPage completedPage = handIDPage.next(randomImageResourcePath());

        completedPage.doBack();

        List<LoanRequest> requestList = wealthService.listLoanRequests(currentUser().getOpenId());
        assertThat(requestList)
                .isNotEmpty();
        LoanRequest request = requestList.get(0);
        // 项目贷款
        assertThat(request)
                .isInstanceOf(ProjectLoanRequest.class);
        ProjectLoanRequest projectLoanRequest = (ProjectLoanRequest) request;
        assertThat(projectLoanRequest.getApplyAmount())
                .isEqualByComparingTo(new BigDecimal(amount));
        assertThat(projectLoanRequest.getApplyCreditLimitYears())
                .isEqualTo(getSystemService().getProjectLoanCreditLimit());
//        assertThat(projectLoanRequest.getCreditLimitYears())
//                .isEqualTo(getSystemService().getProjectLoanCreditLimit());
        assertThat(projectLoanRequest.getApplyTermDays())
                .isEqualTo(wealthService.nextProjectLoanTerm());
//        assertThat(projectLoanRequest.getApply())
//                .isEqualTo(wealthService.nextProjectLoanTerm());
        assertThat(resourceService.getResource(projectLoanRequest.getLoanData().getBackIdResource()).isReadable())
                .isTrue();
        assertThat(resourceService.getResource(projectLoanRequest.getLoanData().getFrontIdResource()).isReadable())
                .isTrue();
        assertThat(resourceService.getResource(projectLoanRequest.getLoanData().getHandIdResource()).isReadable())
                .isTrue();
        // 个人数据
//        assertThat(projectLoanRequest.getLoanData().getHomeAddress())
//                .isEqualTo();
        // 家庭住址 和单位 未纳入测试体系
        assertThat(projectLoanRequest.getLoanData().getPersonalIncome())
                .isEqualTo(personalIncome);
        assertThat(projectLoanRequest.getLoanData().getFamilyIncome())
                .isEqualTo(familyIncome);
        assertThat(projectLoanRequest.getLoanData().getAge())
                .isEqualTo(age);
        assertThat(projectLoanRequest.getLoanData().isHasHouse())
                .isEqualTo(hasHouse);

        assertThat(request.getProcessStatus())
                .isEqualByComparingTo(LoanRequestStatus.requested);
        assertThat(request.getLoanData().getOwner())
                .isEqualTo(currentUser());
        assertThat(request.getLoanData().getName())
                .isEqualTo(name);
        assertThat(request.getLoanData().getNumber())
                .isEqualTo(number);
        assertThat(request.getLoanData().getAddress().getProvince())
                .isEqualTo(province);
        assertThat(request.getLoanData().getAddress().getCity())
                .isEqualTo(city);
        assertThat(request.getAmount())
                .isEqualByComparingTo(new BigDecimal(amount));
        assertThat(request.getProjectId())
                .isEqualTo(loan.getProductId());
//        assertThat(request.getMonths())
//                .isGreaterThan(0);
        // 管理员 登录 并且同意这个借款请求
        assertThat(request.getSupplierRequestId()).isNull();

        // 审批通过这个请求
        final int realTermDays = projectLoanRequest.getApplyTermDays();
        final BigDecimal realAmount = request.getAmount();
        final BigDecimal realYearRate = getSystemService().getProjectLoanYearRate();
        wealthService.approveProjectLoanRequest(null, request.getId(), realAmount, realYearRate, realTermDays, "");
        projectLoanRequest = (ProjectLoanRequest) loanRequestRepository.getOne(request.getId());
        assertThat(projectLoanRequest.getTermDays())
                .isEqualTo(realTermDays);
        assertThat(projectLoanRequest.getAmount())
                .isEqualByComparingTo(realAmount);
        assertThat(projectLoanRequest.getYearRate())
                .isEqualByComparingTo(realYearRate);
        assertThat(projectLoanRequest.getSupplierRequestId()).isNotEmpty();

        // 打开通知所指向的地址
        // 就可以玩一玩签单流程了
        // TODO

    }

    /**
     * 存在2种流程
     * 1,是{@link me.jiangcai.dating.model.trj.ProjectLoan} 固定金额,固定周期,固定汇率 更丰富的个人信息,身份证,银行卡,完成
     * 2,普通的 输入金额,明细,完成
     *
     * @throws IOException
     */
    @Test
    public void loan() throws IOException {
        MyPage myPage = myPage();
        Loan[] loanList = wealthService.loanList();
//        System.out.println(Arrays.toString(loanList));

        LoanPage loanPage = myPage.toLoanPage();
        loanPage.assertList(loanList);

        // 选择了一款普通产品
        Loan loan = Stream.of(loanList).filter(loan1 -> !(loan1 instanceof ProjectLoan))
                .max(new RandomComparator())
                .orElse(null);
        LoanAmountPage page = loanPage.choose(loan.getProductName());

        page.checkAgreement();
        page.assertLoan(loan, 0);
        // 随机从50000-max
        int amount = 50000 + random.nextInt((loan.getAmountInteger() - 50000));
        String term = loan.getTerm()[random.nextInt(loan.getTerm().length)];
        LoanSubmitPage submitPage = page.loan(amount, term);

        //填入姓名,身份证号码, 随便弄一个地址
        String name = RandomStringUtils.randomAscii(2 + random.nextInt(3));
        String number = RandomStringUtils.randomNumeric(18);

        Province province = null;
        while (province == null || districtRepository.byChanpayCode(Locale.CHINA, province.getId()) == null) {
            province = PayResourceService.listProvince().stream().max(new RandomComparator()).orElseThrow(IllegalStateException::new);
        }
        City city = null;
        while (city == null || districtRepository.byChanpayCode(Locale.CHINA, city.getId()) == null) {
            city = province.getCityList().stream().max(new RandomComparator()).orElseThrow(IllegalStateException::new);
        }

        LoanCompletedPage completedPage = submitPage.submitNormal(name, number, province.getName(), city.getName());

        completedPage.doBack();

        List<LoanRequest> requestList = wealthService.listLoanRequests(currentUser().getOpenId());
        assertThat(requestList)
                .isNotEmpty();
        LoanRequest request = requestList.get(0);
        assertThat(request.getProcessStatus())
                .isEqualByComparingTo(LoanRequestStatus.requested);
        assertThat(request.getLoanData().getOwner())
                .isEqualTo(currentUser());
        assertThat(request.getLoanData().getName())
                .isEqualTo(name);
        assertThat(request.getLoanData().getNumber())
                .isEqualTo(number);
        assertThat(request.getLoanData().getAddress().getProvince())
                .isEqualTo(province);
        assertThat(request.getLoanData().getAddress().getCity())
                .isEqualTo(city);
        assertThat(request.getAmount())
                .isEqualByComparingTo(new BigDecimal(amount));
        assertThat(request.getProjectId())
                .isEqualTo(loan.getProductId());
        assertThat(request.getMonths())
                .isGreaterThan(0);
        // 管理员 登录 并且同意这个借款请求
        assertThat(request.getSupplierRequestId()).isNull();

        // uri 规定下
    }

    @Test
    public void financing() throws Exception {
        MyPage myPage = myPage();

//        FinancingPage financingPage = myPage.toFinancingPage();

        try {
            myPage.toFinancingPage();
        } catch (Throwable throwable) {

        }
//        financingPage.assertFinancing(wealthService.currentFinancing());
//        // 我这边点击 肯定是会提示让我输入验证码
//        financingPage.goFinancing();
//        // 这个是一个新手机号码 所以应该是登录界面
//        financingPage.assertLoginPage();

        //先把手机号码是我的给删了!
        User existing = userRepository.findByMobileNumber("18606509616");
        if (existing != null) {
            userRepository.delete(existing);
        }

        User current = currentUser();
        current.setMobileNumber("18606509616");
        userRepository.save(current);

        // 重来
        myPage = myPage();
        FinancingPage financingPage = myPage.toFinancingPage();
        financingPage.assertFinancing(wealthService.currentFinancing());
        financingPage.goFinancing();
        financingPage.assertWorkingPage();

    }

}