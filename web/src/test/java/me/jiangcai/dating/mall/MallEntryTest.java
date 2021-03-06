package me.jiangcai.dating.mall;

import me.jiangcai.dating.WebTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 商城入口测试
 *
 * 静态商城已移除项目
 *
 * @author CJ
 */
@Ignore
public class MallEntryTest extends WebTest {

    @Test
    public void entry() throws Exception {
        mockMvc.perform(get("/mart/martindex.html"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/carinsurance.html"))
                .andDo(print())
                .andExpect(status().isOk());

    }

}
