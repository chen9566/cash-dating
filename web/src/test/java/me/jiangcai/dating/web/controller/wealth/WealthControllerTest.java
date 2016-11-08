package me.jiangcai.dating.web.controller.wealth;

import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.page.FinancingPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.WealthService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author CJ
 */
public class WealthControllerTest extends LoginWebTest {

    @Autowired
    private WealthService wealthService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void loan() throws IOException {
        MyPage myPage = myPage();
        Loan[] loenList = wealthService.loanList();
        System.out.println(Arrays.toString(loenList));
    }

    @Test
    public void financing() throws Exception {
        MyPage myPage = myPage();

        FinancingPage financingPage = myPage.toFinancingPage();

        financingPage.assertFinancing(wealthService.currentFinancing());
        // 我这边点击 肯定是会提示让我输入验证码
        financingPage.goFinancing();
        // 这个是一个新手机号码 所以应该是登录界面
        financingPage.assertLoginPage();

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
        financingPage = myPage.toFinancingPage();
        financingPage.goFinancing();
        financingPage.assertWorkingPage();

    }

}