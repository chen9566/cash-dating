package me.jiangcai.dating.loader;

import me.jiangcai.dating.WebTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author CJ
 */
public class DispatcherServletInitializerTest extends WebTest {

//    @Test
//    public void hello() throws Exception {
//        mockMvc.perform(post("/"))
//                .andDo(print());
//    }

    @Test
    public void weixin() throws Exception {
//        redirectTo(mockMvc.perform(getWeixin("/login").session(session)), session);
        mockMvc.perform(getWeixin("/login?code=0416H2Ac23jicS0HwtAc2Z6Zzc26H2AB&state="))
                .andDo(print());
        // 不可以是302
    }
//
//    @Test
//    public void pages() throws IOException {
//        helloNewUser(null,false);
//        driver.get("http://localhost/"+ UUID.randomUUID().toString());
//        System.out.println(driver.getPageSource());
//    }

}