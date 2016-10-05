package me.jiangcai.dating.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.jiangcai.dating.entity.Card;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 我的银行卡,目前只支持一张卡
 *
 * @author CJ
 */
public class MyBankPage extends AbstractPage {

    private List<BankCard> bankCards;

    /**
     * 检查 只有这么几个卡
     *
     * @param cards
     */
    public void assertCard(List<Card> cards) {
        // 找到一个 则移除之
        ArrayList<BankCard> bankCards = new ArrayList<>(this.bankCards);

        assertThat(bankCards)
                .hasSize(cards.size());

        assertThat(bankCards.stream()
                .filter(bankCard -> in(bankCard, cards))
                .count())
                .isEqualTo(0);

    }

    private boolean in(BankCard bankCard, List<Card> cards) {
        return cards.stream()
                .filter(bankCard::check)
                .count() > 0;
    }

    @Data
    @AllArgsConstructor
    private class BankCard {

        private String bankName;
        private String number;

        boolean check(Card card) {
            //number最后4位
            String end = number.substring(number.length() - 4);
            return card.getBank().getName().equals(bankName) && card.getNumber().endsWith(end);
        }
    }

    public MyBankPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        bankCards = webDriver.findElements(By.className("bankcard")).stream()
                .filter(WebElement::isDisplayed)
                .map(this::toCard)
                .collect(Collectors.toList());

        assertThat(bankCards)
                .isNotEmpty();
    }

    private BankCard toCard(WebElement element) {
        return new BankCard(element.findElement(By.className("txt")).getText()
                , element.findElement(By.className("code-n")).getText());
    }
}
