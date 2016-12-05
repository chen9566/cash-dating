package me.jiangcai.dating.web.controller.wealth;

import me.jiangcai.chanpay.model.City;
import me.jiangcai.chanpay.model.Province;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.page.BindingCardPage;
import me.jiangcai.dating.page.FinancingPage;
import me.jiangcai.dating.page.LoanAmountPage;
import me.jiangcai.dating.page.LoanCompletedPage;
import me.jiangcai.dating.page.LoanIDPage;
import me.jiangcai.dating.page.LoanPage;
import me.jiangcai.dating.page.LoanSubmitPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.WealthService;
import me.jiangcai.gaa.sdk.repository.DistrictRepository;
import me.jiangcai.lib.resource.service.ResourceService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
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
    @Autowired
    private ResourceService resourceService;

    @Test
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

        LoanIDPage idPage = submitPage.submit(name, number, province.getName(), city.getName());

        BindingCardPage bindingCardPage = idPage.next(randomImageResourcePath(), randomImageResourcePath());
//        bindingCardPage.printThisPage();
        bindingCardPage.submitWithRandomAll();

//        LoanCompletedPage completedPage = submitPage.submit(name, number, province.getName(), city.getName());
        LoanCompletedPage completedPage = initPage(LoanCompletedPage.class);

        completedPage.doBack();

        List<LoanRequest> requestList = wealthService.listLoanRequests(currentUser().getOpenId());
        assertThat(requestList)
                .isNotEmpty();
        LoanRequest request = requestList.get(0);
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
    }

    /**
     * @return 随机生成的图片资源路径
     */
    private String randomImageResourcePath() throws IOException {
        String name = "tmp/" + UUID.randomUUID().toString() + ".png";
        resourceService.uploadResource(name, this.applicationContext.getResource("/images/1.png").getInputStream());
        return name;
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