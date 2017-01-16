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
        makeTicketTrade(user, random.nextInt(5) + 1, random.nextInt(5) + 1, random.nextInt(5) + 1);

        MySalePage myPage = mySalePage();

        myPage.printThisPage();
    }

}