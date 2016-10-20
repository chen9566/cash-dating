package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.support.ManageStatus;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.all)
public class UserControllerTest extends ManageWebTest {

    @Test
    public void userData() throws Exception {
        MockHttpSession session = mvcLogin();

        mockMvc.perform(getWeixin("/manage/data/user")
                .param("sort", "nickname")
                .param("order", "asc")
                .param("offset", "0")
                .param("limit", "10").session(session))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(getWeixin("/manage/data/user")
                .param("sort", "nickname")
                .param("order", "desc")
                .param("offset", "0")
                .param("limit", "10").session(session))
                .andDo(print())
                .andExpect(status().isOk());

    }

}