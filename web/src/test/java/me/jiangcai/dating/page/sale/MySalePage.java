package me.jiangcai.dating.page.sale;

import com.google.common.base.Predicate;
import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MySalePage extends AbstractPage {
    private WebElement allOrdersLink;
    private WebElement waitingPayOrdersLink;
    private WebElement waitingSendOrdersLink;
    private WebElement waitingReceiveOrdersLink;

    public MySalePage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("我的");
    }

    /**
     * 可用的电子券
     *
     * @param count
     */
    public void assertUsableTicket(int count) {
        assertThat(webDriver.findElement(By.className("nouse")).findElements(By.tagName("ul")))
                .hasSize(count);
    }

    /**
     * 已用过的电子券
     *
     * @param count
     */
    public void assertUsedTicket(int count) {
        assertThat(webDriver.findElement(By.className("douse")).findElements(By.tagName("ul")))
                .hasSize(count);
    }

    public void clickUsableOneAndUseIt() {
        final WebElement targetButton = webDriver.findElement(By.className("nouse")).findElements(By.tagName("ul")).stream()
                .peek(System.out::println)
                .max(new ServiceBaseTest.RandomComparator())
                .orElse(null);
        targetButton
                .click();

        try {
            new WebDriverWait(webDriver, 10)
                    .until((Predicate<WebDriver>) input
                            -> input != null && input.getTitle().equals("我的优惠券"));
        } catch (TimeoutException exception) {
            targetButton
                    .click();
//            printThisPage();
//            throw exception;
        }

        assertTitle("我的优惠券");
        // 点击 button 立即使用
        webDriver.findElements(By.tagName("button")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getText().equals("立即使用"))
                .findFirst().orElseThrow(() -> new IllegalStateException("找不到使用按钮"))
                .click();

        TicketTradeSuccessPage.clickConfirm(webDriver);
        TicketTradeSuccessPage.findTicketCode(webDriver);

        webDriver.navigate().back();
    }

    public void clickUsedOneAndBack() {
        // 点击 已使用
        webDriver.findElement(By.className("n")).click();

        //
        webDriver.findElement(By.className("douse")).findElements(By.tagName("ul")).stream()
                .max(new ServiceBaseTest.RandomComparator())
                .orElse(null)
                .click();

        assertTitle("我的优惠券");

        webDriver.navigate().back();
    }

    public OrderListPage allOrders() {
        return toOrderListPage(allOrdersLink, "allOrdersLink");
    }

    public OrderListPage waitingPayOrders() {
        return toOrderListPage(waitingPayOrdersLink, "waitingPayOrdersLink");
    }

    public OrderListPage waitingSendOrders() {
        return toOrderListPage(waitingSendOrdersLink, "waitingSendOrdersLink");
    }

    public OrderListPage waitingReceiveOrders() {
        return toOrderListPage(waitingReceiveOrdersLink, "waitingReceiveOrdersLink");
    }

    private OrderListPage toOrderListPage(WebElement linkElement, String id) {
        linkElement.click();
        new WebDriverWait(webDriver, 5).until((Predicate<WebDriver>) input -> {
            if (input == null)
                return false;
            if (input.getTitle().equalsIgnoreCase("我的")) {
                linkElement.click();
                return false;
            }
            return !input.getTitle().equalsIgnoreCase("我的");
        });
        OrderListPage page = initPage(OrderListPage.class);

        page.assertLinkIsCurrent(id);
        return page;
    }

}
