package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.support.ManageStatus;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.all)
public class UserControllerTest extends ManageWebTest {


    @Test
    public void userData() throws Exception {
        MockHttpSession session = mvcLogin();

        String content = mockMvc.perform(getWeixin("/manage/data/user")
                .param("sort", "nickname")
                .param("order", "asc")
                .param("offset", "0")
                .param("limit", "10").session(session))
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/users.json"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(content);

        content = mockMvc.perform(getWeixin("/manage/data/user")
                .param("sort", "nickname")
                .param("order", "desc")
                .param("offset", "0")
                .param("limit", "10").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(content);

        content = mockMvc.perform(getWeixin("/manage/data/user")
                .param("sort", "nickname")
                .param("order", "desc")
                .param("search", "5")
                .param("offset", "0")
                .param("limit", "10").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(content);

    }

}