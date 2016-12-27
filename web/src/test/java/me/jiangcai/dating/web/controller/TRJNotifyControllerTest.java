package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.WebTest;
import org.junit.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * @author CJ
 */
public class TRJNotifyControllerTest extends WebTest {

    @Test
    public void test() throws Exception {
        mockMvc.perform(put("/trj/notify/ItemLoan/1/reject"))
                .andDo(MockMvcResultHandlers.print());
    }


}