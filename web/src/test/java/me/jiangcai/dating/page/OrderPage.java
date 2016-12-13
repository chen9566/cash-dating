package me.jiangcai.dating.page;

import com.google.common.base.Predicate;
import me.jiangcai.dating.channel.ArbitrageChannel;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.CashOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class OrderPage extends AbstractPage {

    private WebElement addCardLink;

    public OrderPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        printThisPage();
//        System.out.println(webDriver.getPageSource());
        assertThat(webDriver.getTitle())
                .isEqualTo("我的订单");

        webDriver.findElements(By.className("addcard")).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .ifPresent(webElement -> {
                    webElement.findElements(By.tagName("a")).stream()
                            .findFirst()
                            .ifPresent(webElement1 -> addCardLink = webElement1);
                });
    }

//    /**
//     * 目前没有银行卡
//     */
//    public void assertWithoutCards() {
//        assertThat(addCardLink)
//                .isNotNull();
//        assertThat(addCardLink.isDisplayed())
//                .isTrue();
//    }

//    /**
//     * 这会儿有卡了
//     */
//    public void assertCards() {
//        assertThat(webDriver.findElements(By.className("addcard")))
//                .isEmpty();
//    }

    /**
     * 点击加卡链接
     */
    public void toCreateNewCard() {
        addCardLink.click();
    }

    /**
     * 更换一个订单的银行卡
     *
     * @param orderId 订单号
     * @param card    卡
     * @deprecated 没了
     */
    public void choseCard(String orderId, Card card) {
        WebElement chooser = webDriver.findElement(By.id("cardsContainer"));
        assertThat(chooser.isDisplayed())
                .isFalse();
        webDriver.findElements(By.className("orderFlow")).stream()
                .filter(WebElement::isDisplayed)
                .peek(System.out::println)
                .filter(webElement -> orderId.equals(webElement.getAttribute("data-id")))
                .peek(System.out::println)
                .findFirst()
                .ifPresent(webElement -> {
                    webElement.findElements(By.tagName("button")).stream()
                            .filter(WebElement::isDisplayed)
                            .filter(webElement1 -> webElement1.getText().contains("银行卡"))
                            .findFirst()
                            .orElse(webElement.findElements(By.tagName("a")).stream()
                                    .filter(WebElement::isDisplayed)
                                    .filter(webElement1 -> webElement1.getText().contains("提现"))
                                    .findFirst()
                                    .orElseThrow(IllegalStateException::new)
                            ).click();
                });

        WebDriverWait wait = new WebDriverWait(webDriver, 2);
        wait.until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver input) {
                return input != null && input.findElement(By.id("cardsContainer")).isDisplayed();
            }
        });
        // 这个时候应该展示了一个
        assertThat(chooser.isDisplayed())
                .isTrue();
        WebElement cardSelect = webDriver.findElement(By.name("card"));
        cardSelect.findElements(By.tagName("option")).stream()
                .filter(webElement -> {
                    String str = webElement.getText();
                    return str.contains(card.getBank().getName()) && str.contains(card.getTailNumber());
                })
                .findFirst()
                .ifPresent(WebElement::click);

        chooser.findElement(By.className("cardSubmit")).click();
    }

    /**
     * 重新尝试提现一张订单
     *
     * @param orderId 订单号
     */
    public void retry(String orderId) {
//        webDriver.findElements(By.className("orderFlow")).stream()
//                .filter(WebElement::isDisplayed)
//                .filter(webElement -> orderId.equals(webElement.getAttribute("data-id")))
//                .findFirst()
//                .ifPresent(webElement -> {
//                    webElement.findElements(By.tagName("button")).stream()
//                            .filter(WebElement::isDisplayed)
//                            .filter(webElement1 -> webElement1.getText().contains("重试"))
//                            .findFirst()
//                            .ifPresent(WebElement::click);
//                });
        webDriver.findElements(By.tagName("li")).stream()
                .filter(WebElement::isDisplayed)
                .filter(webElement -> webElement.getAttribute("href").endsWith(orderId))
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .click();

        OrderDetailPage detailPage = initPage(OrderDetailPage.class);
        detailPage.retry();
    }

    public void assertSuccessOrder(int index, CashOrder order) {
        WebElement element = checkOrder(index, order);
        assertThat(element.findElements(By.tagName("span")).stream()
                .filter(webElement -> webElement.getText().contains("到账"))
                .findAny()
                .orElse(null)
        ).isNotNull();
        element.click();
        OrderDetailPage detailPage = initPage(OrderDetailPage.class);
        detailPage.assertSuccess(order);
        webDriver.navigate().back();
        reloadPageInfo();
    }

    private WebElement checkOrder(int index, CashOrder order) {
        WebElement element = webDriver.findElements(By.tagName("li")).get(index);
        System.out.println(element.findElement(By.className("f17")).getText());
        System.out.println(element.getAttribute("href"));
        assertThat(element.getAttribute("href"))
                .endsWith(order.getId());
        return element;
    }

    public void assertTransferringOrder(int index, CashOrder order) {
        WebElement element = checkOrder(index, order);
        assertThat(element.findElements(By.tagName("span")).stream()
                .filter(webElement -> webElement.getText().contains("转账中"))
                .findAny()
                .orElse(null)
        ).isNotNull();

        element.click();
        OrderDetailPage detailPage = initPage(OrderDetailPage.class);
        detailPage.assertTransferring(order);
        webDriver.navigate().back();
        reloadPageInfo();
    }

    public void assertFailedOrder(int index, CashOrder order, ArbitrageChannel channel) {
        WebElement element = checkOrder(index, order);
        assertThat(element.findElements(By.tagName("span")).stream()
                .filter(webElement -> webElement.getText().contains("未到账"))
                .findAny()
                .orElse(null)
        ).isNotNull();

        element.click();
        OrderDetailPage detailPage = initPage(OrderDetailPage.class);
        detailPage.assertFailed(order, channel);
        webDriver.navigate().back();
        reloadPageInfo();
    }
}
