package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.page.mall.IndexPage;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author CJ
 */
public class IndexControllerTest extends WebTest {

    @Test
    public void home() throws Exception {
        mockMvc.perform(get("/mall/"))
                .andDo(MockMvcResultHandlers.print());

        driver.get("http://localhost/mall/");
        IndexPage indexPage = initPage(IndexPage.class);

        indexPage.printThisPage();
    }

}