package me.jiangcai.dating.web.controller.pay;

import com.google.zxing.WriterException;
import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.repository.CashOrderRepository;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

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

    @Test
    public void to() throws IOException, WriterException {
        // 应该是由 我的页面 开始进入,现在程序错误 我们直接拿Image
        WeixinUserDetail userDetail = createNewUser();

        User user = userService.byOpenId(userDetail.getOpenId());
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        this.req
        String comment = UUID.randomUUID().toString() + "中";
        BufferedImage image = payController.payToQRCode(user.getId(), comment, request);
        tryPayTo(image, user, comment);
    }

    private void tryPayTo(BufferedImage image, User user, String comment) {
        // 模拟支付
        // TODO:还是不行 缺少填写金额
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