package me.jiangcai.dating.page.sale;

import com.google.common.base.Predicate;
import me.jiangcai.dating.page.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * myorder.html
 * 我的订单
 *
 * @author CJ
 */
public class OrderListPage extends AbstractPage {
    public OrderListPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualToIgnoringCase("我的订单");

        new WebDriverWait(webDriver, 5).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver input) {
                if (input == null)
                    return false;
                return !input.findElements(By.className("noorder")).isEmpty()
                        || !input.findElements(By.className("cashTrade")).isEmpty();
            }
        });
    }

    /**
     * 移动到最后面
     *
     * @return 总订单数量
     */
    public int count() {
        while (true) {
            List<WebElement> cashTradeList = webDriver.findElements(By.className("cashTrade"));
            if (cashTradeList.isEmpty())
                return 0;
            new Actions(webDriver)
                    .moveToElement(cashTradeList.get(cashTradeList.size() - 1))
                    .build().perform();
            // 如果长度不变 则就是最终值了
            new WebDriverWait(webDriver, 5).until((Predicate<WebDriver>) input -> {
                try {
                    return input != null && (input.findElement(By.name("LoadingIMG")) == null || !input.findElement(By.name("LoadingIMG")).isDisplayed());
                } catch (Throwable elementException) {
                    return true;
                }
            });
            List<WebElement> newCashTradeList = webDriver.findElements(By.className("cashTrade"));
            if (newCashTradeList.size() == cashTradeList.size())
                return cashTradeList.size();
        }
    }

    public void assertLinkIsCurrent(String id) {
        assertThat(webDriver.findElement(By.id(id)).getAttribute("class"))
                .contains("current");
    }

    public void openAllStatus() {
        ArrayList<String> used = new ArrayList<>();

        while (true) {
            int size = used.size();
            count();
            Map<String, List<WebElement>> tradeByStatus = webDriver.findElements(By.className("cashTrade")).stream()
                    .collect(Collectors.groupingBy(this::status));

            tradeByStatus.keySet().stream()
                    .filter(s -> !used.contains(s))
                    .findAny()
                    .ifPresent(status -> {
                        used.add(status);
                        tradeByStatus.get(status).get(0).findElements(By.tagName("dl")).get(0).click();
                        System.out.println(status);
                        System.out.println(webDriver.getPageSource());
                        webDriver.navigate().back();
                        refresh();
                    });

            if (used.size() == size)
                return;
        }


    }

    public String status(WebElement element) {
        return element.findElements(By.tagName("span")).stream()
                .filter(WebElement::isDisplayed)
                .filter(element1 -> element1.getAttribute("class") != null && element1.getAttribute("class").startsWith("state"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("找不到状态")).getText();
    }
}
