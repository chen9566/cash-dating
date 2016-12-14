package me.jiangcai.dating.page;

import me.jiangcai.dating.entity.Card;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 开始收款页面
 * 1.5 更新至可以换卡,但不可以加卡,同样无需把卡号传过来,所谓更换就是禁用原卡
 * receivables.html
 *
 * @author CJ
 */
public class StartOrderPage extends AbstractPage {

    private static final Log log = LogFactory.getLog(StartOrderPage.class);

    private WebElement amountInput;
    private WebElement commentInput;
    @FindBy(id = "_btn1")
    private WebElement button;
    private WebElement cardChanger;

    public StartOrderPage(WebDriver webDriver) {
        super(webDriver);
//        System.out.println(webDriver.getPageSource());
    }

    @Override
    public void validatePage() {
        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> "amount".equals(element.getAttribute("name")) && element.isDisplayed())
                .findAny()
                .ifPresent(element -> amountInput = element);

        webDriver.findElements(By.tagName("input")).stream()
                .filter(element -> "comment".equals(element.getAttribute("name")) && element.isDisplayed())
                .findAny()
                .ifPresent(element -> commentInput = element);

//        webDriver.findElements(By.tagName("button")).stream()
//                .filter(element -> "submit".equals(element.getAttribute("type")) && element.isDisplayed())
//                .findAny()
//                .ifPresent(element -> button = element);

        assertThat(amountInput)
                .isNotNull();
        assertThat(amountInput.isDisplayed()).isTrue();
        assertThat(commentInput)
                .isNotNull();
        assertThat(button)
                .isNotNull();
        assertThat(button.isDisplayed())
                .isTrue();
    }

    public ShowOrderPage pay(double amount, String comment, Predicate<WebElement> cardChooser) {
        if (cardChooser != null) {
            WebElement all = webDriver.findElement(By.className("all-cards"));
            assertThat(all.isDisplayed())
                    .isFalse();
            // 打开列表 选择卡
            assertThat(this.cardChanger.isDisplayed())
                    .isTrue();
            // .all-cards
            cardChanger.click();

            all.findElements(By.className("card")).stream()
                    .filter(WebElement::isDisplayed)
                    .filter(cardChooser)
                    .findFirst()
                    .ifPresent(card -> card.findElement(By.name("cardChooser")).click());
            WebDriverWait wait = new WebDriverWait(webDriver, 2);
            wait.until(new com.google.common.base.Predicate<WebDriver>() {
                @Override
                public boolean apply(@Nullable WebDriver input) {
                    if (input == null)
                        return false;
                    return !input.findElement(By.className("all-cards")).isDisplayed();
                }
            });
            assertThat(all.isDisplayed())
                    .isFalse();
        }
        amountInput.clear();

        NumberFormat format = NumberFormat.getNumberInstance();
        format.setRoundingMode(RoundingMode.HALF_UP);//设置四舍五入
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(false);

        log.debug(amount);
        log.debug(format.format(amount));
        amountInput.sendKeys(format.format(amount));
//        commentInput.clear();
//        commentInput.sendKeys(comment);
        button.click();
        return initPage(ShowOrderPage.class);
    }

    public void assertNoCard() {
        assertThat(webDriver.findElements(By.className("nocard")))
                .isNotEmpty();
    }

    public void assertHaveCard() {
        assertThat(webDriver.findElements(By.className("card")))
                .isNotEmpty();
    }

    /**
     * 去建立唯一的一个卡
     */
    public void toCreateNewOneCard() {
        WebElement host;
        if (webDriver.findElements(By.className("nocard")).isEmpty()) {
            host = webDriver.findElement(By.className("card"));
        } else
            host = webDriver.findElement(By.className("nocard"));

        host.findElement(By.tagName("a")).click();
    }

    /**
     * @param card 确认显示了这张卡
     */
    public void assertCard(Card card) {
        WebElement cardElement = webDriver.findElement(By.className("selectedCard"));
//        System.out.println(cardElement.getText());
        assertThat(cardElement.getText()).contains(card.getTailNumber());
    }
}
