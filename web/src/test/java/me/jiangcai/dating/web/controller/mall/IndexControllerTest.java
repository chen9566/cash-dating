package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.RootAuthentication;
import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.support.FakeCategory;
import me.jiangcai.dating.page.mall.IndexPage;
import me.jiangcai.dating.page.mall.LoginPage;
import me.jiangcai.dating.page.mall.RegisterPage;
import me.jiangcai.dating.service.sale.MallGoodsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class IndexControllerTest extends WebTest {

    @Autowired
    private MallGoodsService mallGoodsService;

    @Test
    public void index() throws IOException {
        int count = 200;
        while (count-- > 0)
            addRandomFakeGoods();
        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class);

        indexPage.printThisPage();
    }

    private void addRandomFakeGoods() throws IOException {
        RootAuthentication.runAsRoot(() -> {
            FakeGoods goods;
            try {
                goods = mallGoodsService.addFakeGoods(UUID.randomUUID().toString(), randomOrderAmount().toString());
            } catch (IOException e) {
                throw new InternalError(e);
            }
            // 设定其属性
            CashGoods cashGoods = randomGoodsData();

            goods.setFakeCategory(FakeCategory.values()[random.nextInt(FakeCategory.values().length)]);
            goods.setSales(random.nextInt(100) + 1);
            goods.setStock(random.nextInt(100) + 1);

            goods.setSubPrice(cashGoods.getSubPrice());
            goods.setRichDetail(cashGoods.getRichDetail());
            goods.setPrice(cashGoods.getPrice());
            goods.setBrand(cashGoods.getBrand());
            goods.setDescription(cashGoods.getDescription());
            goods.setName(cashGoods.getName());

            mallGoodsService.saveGoods(goods);
        });

    }

    @Test
    public void login() throws Exception {
        mockMvc.perform(get("/mall/"))
//                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
        ;

        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class);

//        indexPage.printThisPage();

        indexPage.assertNotLogin();

        // 现在打开注册页面
        RegisterPage registerPage = indexPage.openRegisterPage();

        // 填写 手机号码，然后点击验证码，填入验证码，密码，确认密码
        String mobile = randomMobile();
        String password = randomMobile();
        registerPage.registerAsRandom(mobile, password);

//        System.out.println(driver.getPageSource());
        // 开始进入登录流程
        indexPage = initPage(IndexPage.class);
        LoginPage loginPage = indexPage.openLoginPage();

        indexPage = loginPage.loginAs(mobile, password);

//        System.out.println(driver.getPageSource());
        indexPage.assertLogin();
    }

}