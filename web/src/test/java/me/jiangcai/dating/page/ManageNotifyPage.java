package me.jiangcai.dating.page;

import com.google.common.base.Predicate;
import me.jiangcai.dating.notify.NotifyType;
import org.apache.http.NameValuePair;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class ManageNotifyPage extends AbstractPage {

    public ManageNotifyPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertThat(webDriver.getTitle())
                .isEqualTo("消息管理");
    }

    /**
     * 保存一个类型
     *
     * @param type       具体的业务
     * @param enable     是否启用
     * @param title      标题
     * @param shortId    库id
     * @param templateId 模板id
     */
    public void openNotify(NotifyType type, boolean enable, String title, String shortId, String templateId) {
        WebElement element = getNotifyRegion(type);
        WebElement form = element.findElement(By.tagName("form"));
        inputChecked(form, "enabled", enable);
        inputText(form, "title", title);
        inputText(form, "shortId", shortId);
        inputText(form, "templateId", templateId);
        element.findElement(By.className("updateMessage")).click();
    }


    /**
     * 在页面上填入参数的模板
     *
     * @param type               具体的业务
     * @param parameterTemplates 模板
     */
    public void updateParameterTemplates(NotifyType type, List<NameValuePair> parameterTemplates) {
        WebElement element = getNotifyRegion(type);
        WebElement messageRegion = element.findElement(By.className("parameterTemplate"));
        parameterTemplates.forEach(nameValuePair
                -> messageRegion.findElements(By.name(nameValuePair.getName())).stream()
                .filter(webElement -> webElement.getTagName().equals("input"))
                .findFirst()
                .ifPresent(inputTag -> {
                    inputTag.clear();
                    inputTag.sendKeys(nameValuePair.getValue());
                }));
    }

    /**
     * 发送预览
     *
     * @param type 具体的业务
     * @param vars 业务参数
     */
    public void previewNotify(NotifyType type, List<String> vars) {
        WebElement element = getNotifyRegion(type);
        element.findElement(By.className("previewNotify")).click();
        WebElement modal = element.findElement(By.className("modal"));
        new WebDriverWait(webDriver, 2)
                .until(new Predicate<WebDriver>() {
                    @Override
                    public boolean apply(@Nullable WebDriver input) {
                        if (input == null)
                            return false;
                        return modal.isDisplayed();
                    }
                });
        assertThat(modal.isDisplayed())
                .isTrue();

        for (int i = 0; i < vars.size(); i++) {
            String index = "" + i;
            String var = vars.get(i);
            modal.findElements(By.name(index)).stream()
                    .filter(webElement -> webElement.getTagName().equals("input"))
                    .findFirst()
                    .ifPresent(inputTag -> {
                        inputTag.clear();
                        inputTag.sendKeys(var);
                    });
        }

        modal.findElement(By.className("previewSubmit")).click();
        assertNoAlert();
    }

    /**
     * 保存参数模板信息
     *
     * @param type 具体的业务
     */
    public void saveParameterTemplates(NotifyType type) {
        WebElement element = getNotifyRegion(type);
        element.findElement(By.className("updateParameter")).click();
        assertNoAlert();
    }

    private void assertNoAlert() {
        try {
            throw new AssertionError(webDriver.switchTo().alert().getText());
        } catch (NoAlertPresentException ignored) {
        }
    }


    private WebElement getNotifyRegion(NotifyType type) {
        WebElement region = webDriver.findElement(By.cssSelector(".messageTemplate[name=" + type.name() + "]"));
        webDriver.findElement(By.cssSelector("h3[name=" + type.name() + "]")).click();
        return region;
    }
}
