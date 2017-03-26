package me.jiangcai.dating.page.mall;

import me.jiangcai.dating.entity.sale.FakeGoods;
import me.jiangcai.dating.entity.sale.support.FakeCategory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author CJ
 */
public abstract class GoodsListPage extends AbstractMallPage {

    @FindBy(className = "_goodsList")
    private WebElement goodsContainer;
    @FindBy(className = "clickForCategory")
    private WebElement clickForCategory;

    GoodsListPage(WebDriver webDriver) {
        super(webDriver);
    }

    public FakeGoodsDetailPage openDetailPage(Comparator<Object> comparator) {
        webDriver.findElements(By.className("clickHref")).stream()
                .sorted(comparator)
                .findFirst().orElseThrow(() -> new IllegalStateException("什么都没找到"))
                .click();
        return initPage(FakeGoodsDetailPage.class);
    }

    /**
     * 必然而且只包含这些商品
     *
     * @param goodsCollection 商品
     */
    public void assertHaveGoods(Collection<FakeGoods> goodsCollection) {
        assertThat(goodsContainer.findElements(By.tagName("li")).stream()
                .map(element
                        -> element.findElement(By.className("f14")).getText())
                .collect(Collectors.toList())
        )
                .containsOnlyElementsOf(
                        goodsCollection.stream()
                                .map(FakeGoods::getName)
                                .collect(Collectors.toList())
                );
    }

    public SearchPage openCategory(FakeCategory category) {
        clickForCategory.findElements(By.tagName("a")).stream()
                .filter(element -> element.getText().equals(category.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("找不到类目" + category + "的入口"))
                .click();
        return initPage(SearchPage.class);
    }
}
