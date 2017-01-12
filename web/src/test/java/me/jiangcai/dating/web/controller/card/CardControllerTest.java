package me.jiangcai.dating.web.controller.card;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.PayMethod;
import me.jiangcai.dating.page.BindingCardPage;
import me.jiangcai.dating.page.MyBankPage;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.ShowOrderPage;
import me.jiangcai.dating.page.StartOrderPage;
import me.jiangcai.dating.repository.CashOrderRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 新用户注册后，收款页面点进的
 * 上面的加号
 *
 * @author CJ
 */
public class CardControllerTest extends WebTest {

    private static final Log log = LogFactory.getLog(CardControllerTest.class);
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;

    @Test
    public void flowNewUser() throws IOException {
        // 这个流程只有是单独体系才有的
        if (getSystemService().arbitrageChannel(PayMethod.weixin).useOneOrderForPayAndArbitrage()) {
            log.info("无需这个测试流程");
            return;
        }

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
        orderPage.assertHaveCard();
        // 这里可以选择去添加卡 继续添加银行卡
    }

    @Test
    public void anotherUser() throws IOException {
        // 它是先建立银行卡 再来玩 而且一次性建立了2个
        User user = helloNewUser(null, false);
        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);

        myPage.clickMenu("我的银行卡");
        MyBankPage bankPage = initPage(MyBankPage.class);

        SubBranchBank subBranchBank = randomSubBranchBank();

        String owner = RandomStringUtils.randomAlphanumeric(3);
        String number = randomBankCard();
        // 添加
        bankPage = bindCardOnBankPage(user.getMobileNumber(), bankPage, subBranchBank, owner, number);
        user = userService.byOpenId(user.getOpenId());
        bankPage.assertCard(user.getCards().stream().filter(card -> !card.isDisabled()).collect(Collectors.toList()));
        // again
        subBranchBank = randomSubBranchBank();

        owner = RandomStringUtils.randomAlphanumeric(3);
        number = randomBankCard();
        // 添加
        bankPage = bindCardOnBankPage(user.getMobileNumber(), bankPage, subBranchBank, owner, number);
        user = userService.byOpenId(user.getOpenId());
        assertThat(cardService.recommend(user).getNumber())
                .isEqualTo(number);
//        bankPage.assertCard(user.getCards());
//        assertThat(user.getCards())
//                .hasSize(2);

        // 好了 再去刷卡
        driver.get("http://localhost/start");
        StartOrderPage orderPage = initPage(StartOrderPage.class);

        // 选择卡
        orderPage.assertHaveCard();
        Card exceptedCard = cardService.recommend(user);
        orderPage.assertCard(exceptedCard);

        // 1.5 不再有选卡的功能
        ShowOrderPage codePage = orderPage.pay(100, "", null);

//        orderPage.pay(100, "", webElement -> {
//            return webElement.findElements(By.tagName("span")).stream()
//                    .filter(WebElement::isDisplayed)
//                    .filter(webElement1 -> {
//                        // 要么 等于银行名称 要么 搜索到尾号
//                        return webElement1.getText().equals(exceptedCard.getBank().getName())
//                                || webElement1.getText().contains(exceptedCard.getTailNumber());
//                    })
//                    .count() == 2;
//        });

        // 应该去检查这个订单信息 以确保卡号是一致了
        // 没有意义了
//        CashOrder order = cashOrderRepository.getOne(codePage.orderId());
//        assertThat(order.getCard())
//                .isNotNull()
//                .isEqualTo(exceptedCard);
    }

    // 1.5开始 绑卡就是更换之前的卡
    private MyBankPage bindCardOnBankPage(String mobile, MyBankPage page, SubBranchBank bank, String owner, String number) {
        page.toCreateNewCard();

        BindingCardPage cardPage = initPage(BindingCardPage.class);
        // 这个用户已经产生
        assertThat(userService.byMobile(mobile))
                .isNotNull();
        //
        // 地址自己选吧


        cardPage.submitWithRandomAddress(bank, owner, number, randomPeopleId());
        return initPage(MyBankPage.class);
    }

}