package me.jiangcai.dating.web.controller.pay;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PayToUserOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.OrderFlow;
import me.jiangcai.dating.model.PayMethod;
import me.jiangcai.dating.page.MyPage;
import me.jiangcai.dating.page.PayToMePage;
import me.jiangcai.dating.page.PayToPage;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.dating.service.QRCodeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class PayControllerTest extends WebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;
    @Autowired
    private PayController payController;
    @Autowired
    private QRCodeService qrCodeService;

    @Test
    public void to() throws Exception {
        // 应该是由 我的页面 开始进入,现在程序错误 我们直接拿Image
        User user = helloNewUser(null, true);

        driver.get("http://localhost/my");
        MyPage myPage = initPage(MyPage.class);
        myPage.clickPayToMe();

        // payment.html
        PayToMePage payToMePage = initPage(PayToMePage.class);
        // 拿到url
        String comment = UUID.randomUUID().toString() + "中";
        payToMePage.changeTitle(comment);

//        WeixinUserDetail userDetail = createNewUser();
//
//        User user = userService.byOpenId(userDetail.getOpenId());
////        MockHttpServletRequest request = new MockHttpServletRequest();
////        this.req
//
//        BufferedImage image = payController.payToQRCode(user.getId(), comment, request);
        tryPayTo(payToMePage.checkOutImage(), user, comment);
    }

    private void tryPayTo(BufferedImage image, User user, String comment) throws Exception {
        // 模拟支付

        // 另外用户扫码支付
        String url = qrCodeService.scanImage(image);
        System.out.println(url);
        // 无需用户 只要可以提供openId就成
        driver.quit();
        createWebDriver();
//        User newUser = helloNewUser(null,true);
        driver.get(url);
        PayToPage payToPage = initPage(PayToPage.class);

        String text = "111.11";
        payToPage.pay(text, true, PayMethod.weixin);
        //订单已支付
        OrderFlow flow = orderService.orderFlows(user.getOpenId()).get(0);
        assertThat(flow.getOrder())
                .isInstanceOf(PayToUserOrder.class);
//        assertThat(flow.getOrder().getComment())
//                .isEqualTo(comment);// 技术问题
        assertThat(flow.getOrder().getAmount()).isEqualTo(text);

    }

    @Test
    @Transactional
    public void orderStatus() throws Exception {
        // 所有人都可以检查
        CashOrder cashOrder = new CashOrder();
        cashOrder.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        cashOrder = cashOrderRepository.save(cashOrder);

        mockMvc.perform(get("/orderCompleted/{1}", cashOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        cashOrder.setCompleted(true);
        mockMvc.perform(get("/orderCompleted/{1}", cashOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

}