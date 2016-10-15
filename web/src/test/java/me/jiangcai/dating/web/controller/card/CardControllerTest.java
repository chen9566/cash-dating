package me.jiangcai.dating.web.controller.card;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.StartOrderPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * 新用户注册后，收款页面点进的
 * 上面的加号
 *
 * @author CJ
 */
public class CardControllerTest extends WebTest {

    @Test
    public void flowNewUser() throws IOException {
        User user = helloNewUser(null, false);
        driver.get("http://localhost/start");

        StartOrderPage orderPage = initPage(StartOrderPage.class);
        orderPage.assertNoCard();

        // 建立一个卡
//        orderPage.toCreateNewOneCard();

        SubBranchBank subBranchBank = randomSubBranchBank();

        final String owner = RandomStringUtils.randomAlphanumeric(3);
        final String number = randomBankCard();
        orderPage = bindCardOnOrderPage(user.getMobileNumber(), orderPage, subBranchBank, owner, number);

        // 这里
    }

}