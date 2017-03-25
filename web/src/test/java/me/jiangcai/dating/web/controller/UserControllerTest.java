package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.support.ManageStatus;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                .andExpect(similarDataJsonAs("/mock/users.json"))
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

        System.out.println(currentUser().getNickname());

        mockMvc.perform(getWeixin("/manage/data/user")
                .param("sort", "nickname")
                .param("order", "desc")
                .param("search", currentUser().getNickname())
                .param("offset", "0")
                .param("limit", "10").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.rows[0].completedCashOrders").value(0));
        makeFinishCashOrder(currentUser(), randomOrderAmount(), null);
        mockMvc.perform(getWeixin("/manage/data/user")
                .param("sort", "nickname")
                .param("order", "desc")
                .param("search", currentUser().getNickname())
                .param("offset", "0")
                .param("limit", "10").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.rows[0].completedCashOrders").value(1));


    }

}