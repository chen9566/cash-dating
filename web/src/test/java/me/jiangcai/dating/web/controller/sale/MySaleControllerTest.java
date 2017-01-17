package me.jiangcai.dating.web.controller.sale;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.page.sale.MySalePage;
import org.junit.Test;

/**
 * @author CJ
 */
public class MySaleControllerTest extends WebTest {

    @Test
    public void index() throws Exception {
        // 购买的业务
        User user = helloNewUser(null, true);

        // 下单数个，付款数个，确认数个
        final int ordered = random.nextInt(5) + 1;
        final int paid = random.nextInt(5) + 1;
        final int sures = random.nextInt(5) + 1;
        makeTicketTrade(user, ordered, paid, sures);

        MySalePage myPage = mySalePage();

        myPage.assertUsableTicket(paid + sures);
        myPage.assertUsedTicket(0);

        myPage.clickUsableOneAndUseIt();
        myPage.refresh();
        myPage.assertUsableTicket(paid + sures - 1);
        myPage.assertUsedTicket(1);
//        myPage.printThisPage();
        myPage.clickUsedOneAndBack();

    }

}