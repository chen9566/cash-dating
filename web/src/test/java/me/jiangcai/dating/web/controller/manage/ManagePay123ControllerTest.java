package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.UserPaymentExtend;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.page.AbstractPage;
import me.jiangcai.dating.repository.supplier.Pay123CardRepository;
import me.jiangcai.dating.service.QRCodeService;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpSession;

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@AsManage(ManageStatus.waiter)
public class ManagePay123ControllerTest extends ManageWebTest {

    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private Pay123CardRepository pay123CardRepository;

    @Test
    public void index() {
        driver.get("http://localhost/manage/pay123");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("台卡管理");

        boolean pay123 = getSystemService().isEnablePay123();
        driver.get("http://localhost/manage/pay123/toggle");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("台卡管理");
        assertThat(getSystemService().isEnablePay123())
                .isEqualTo(!pay123);
        driver.get("http://localhost/manage/pay123/toggle");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("台卡管理");
        assertThat(getSystemService().isEnablePay123())
                .isEqualTo(pay123);

        // 这里是数据
        getSystemService().updateEnablePay123(false);
//        System.out.println(driver.getPageSource());
    }

    @Test
    public void data() throws Exception {
        // 走一个流程 一个客户端访问应该是直接产生一个 如果找不到了 应该给予警告
        getSystemService().updateEnablePay123(true);
        try {
            pay123CardRepository.findAllUnused().stream().forEach(pay123CardRepository::delete);
            // 走流程
//            WebDriver pcDriver = MockMvcHtmlUnitDriverBuilder
//                    .mockMvcSetup(mockMvc)
//                    .build();
            startOrderPage();
//            orderPage.printThisPage();

            MockHttpSession session = mvcLogin();
            mockMvc.perform(fileUpload("/manage/pay123")
                    .file("file", org.springframework.util.StreamUtils.copyToByteArray
                            (new ClassPathResource("/pay123.zip").getInputStream()))
                    .session(session))
                    .andExpect(status().isFound());

            long old = pay123CardRepository.countAllUnused();

            try {
                startOrderPage();
            } catch (Throwable ignored) {
            }
            System.out.println(driver.getPageSource());
            BufferedImage image = AbstractPage.toImage(driver.findElement(By.id("qrCode")));
            // 解析出来的结果应该是仓库里的一个值
            String url = qrCodeService.scanImage(image);

            assertThat(pay123CardRepository.findByQrUrl(url))
                    .isNotNull();
            // 可用卡数量应该减少1
            assertThat(pay123CardRepository.countAllUnused())
                    .isEqualTo(old - 1);

            assertThat(currentUser().getUserPaymentExtend())
                    .isNotNull();

            UserPaymentExtend extend = currentUser().getUserPaymentExtend();
            assertThat(extend.getPay123AssignTime())
                    .isNotNull();
            assertThat(extend.getPay123Card())
                    .isNotNull();
            assertThat(extend.getPay123Card().getId())
                    .isNotNull();
            // 当前用户 应该标记为卡扩展
        } finally {
            getSystemService().updateEnablePay123(false);
        }
    }

}