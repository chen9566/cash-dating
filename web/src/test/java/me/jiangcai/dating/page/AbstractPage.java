package me.jiangcai.dating.page;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.service.QRCodeService;
import me.jiangcai.dating.service.SystemService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitWebElement;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public abstract class AbstractPage extends me.jiangcai.lib.test.page.AbstractPage {

    private static final Field elementField;

    static {
        try {
            elementField = HtmlUnitWebElement.class.getDeclaredField("element");
            elementField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new InternalError("炸!,版本更新了?", e);
        }
    }

    public AbstractPage(WebDriver webDriver) {
        super(webDriver);
    }

    /**
     * 你懂的
     *
     * @param element
     * @return
     * @throws IOException
     */
    public static BufferedImage toImage(WebElement element) throws IOException {
        try {
            HtmlImage image = (HtmlImage) elementField.get(element);
            return image.getImageReader().read(0);
        } catch (IllegalAccessException e) {
            throw new InternalError("炸!,版本更新了?", e);
        }
    }

    /**
     * 模拟微信支付
     *
     * @param orderId 我方订单号
     * @param payCode 支付码
     */
    void mockWeixinPay(String orderId, BufferedImage payCode) throws Exception {
        WebTest webTest = (WebTest) getTestInstance();
        CashOrder order = webTest.getOrderService().getOne(orderId);

        String url = webTest.getQrCodeService().scanImage(payCode);
        if (url.startsWith("trade"))
            getWebTest().tradeSuccess(order);
        else if (url.startsWith("http://localhost/")) {
            getWebTest().mockMVC().perform(get(url)).andExpect(status().isOk());
        } else
            webTest.getPay().pay(order.getPlatformOrderSet().iterator().next().getId(), url);
        Thread.sleep(2500);
    }

    protected void assertAlert(String text) {
    }

    public void inputSelect(WebElement formElement, String inputName, String label) {
        Predicate<String> predicate = label::equals;

        inputSelect(formElement, inputName, predicate);
    }

    public void inputSelect(WebElement formElement, String inputName, Predicate<String> predicate) {
        WebElement input = formElement.findElement(By.name(inputName));

        if (input.getAttribute("class") != null && input.getAttribute("class").contains("chosen-select")) {
            // 换一个方式
            WebElement container = formElement.findElements(By.className("chosen-container"))
                    .stream()
                    .filter(webElement -> webElement.getAttribute("title") != null && webElement.getAttribute("title")
                            .equals(input.getAttribute("title")))
                    .findAny().orElseThrow(() -> new IllegalStateException("使用了chosen-select,但没看到chosen-container"));

            container.click();
            for (WebElement element : container.findElements(By.cssSelector("li.active-result"))) {
                if (predicate.test(element.getText())) {
                    element.click();
                    return;
                }
            }
            return;
        }
        //chosen-container chosen-container-single and same title
        // li.active-result

        input.clear();
        for (WebElement element : input.findElements(By.tagName("option"))) {
//            System.out.println(element.getText());
            if (predicate.test(element.getText())) {
                element.click();
                return;
            }
        }
    }

    protected SubBranchBank randomSubBranchBank() {
        return getWebTest().randomSubBranchBank();
    }

    protected String randomBankCard() {
        return getWebTest().randomBankCard();
    }

    protected SystemService getSystemService() {
        return getWebTest().getSystemService();
    }

    protected QRCodeService getQRCodeService() {
        return getWebTest().getQrCodeService();
    }

    private WebTest getWebTest() {
        return (WebTest) (getTestInstance());
    }

    protected void setElementAttributeValue(WebElement element, String attributeName, String value) {
        try {
            Field field = HtmlUnitWebElement.class.getDeclaredField("element");
            field.setAccessible(true);
            HtmlElement htmlHiddenInput = (HtmlElement) field.get(element);
            htmlHiddenInput.setAttribute(attributeName, value);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected void assertTitle(String title) {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase(title);
    }
}
