package me.jiangai.dating.loader;

import me.jiangai.dating.WebTest;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author CJ
 */
public class DispatcherServletInitializerTest extends WebTest {

    @Test
    public void hello() throws Exception {
        mockMvc.perform(post("/"))
                .andDo(print());
    }

}