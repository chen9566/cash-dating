package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.AgentRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.service.AgentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.editor)
public class AgentRequestControllerTest extends ManageWebTest {

    @Autowired
    private AgentService agentService;

    @Test
    public void index() {
        driver.get("http://localhost/manage/agentRequest");
        System.out.println(driver.getPageSource());
        assertThat(driver.getTitle())
                .isEqualTo("审批合伙人");
    }

    @Test
    public void actions() throws Exception {
        //操作
        MockHttpSession session = mvcLogin();

        User user = userService.newUser(UUID.randomUUID().toString().replaceAll("-", ""), null);
        final String name = randomString(10);
        AgentRequest request = agentService.newRequest(user, name, randomMobile());

        // /manage/data/agentRequest/pending
        mockMvc.perform(getWeixin("/manage/data/agentRequest/pending").session(session)
                .param("search", name)
                .param("offset", "0")
                .param("limit", "10")
        )
                .andExpect(status().isOk())
                .andExpect(simliarDataJsonAs("/mock/agentRequest.json"))
                .andDo(print());

        // 操作
        // 拒绝  然后就应该看不到了
        HashMap<String, Object> toCommit = new HashMap<>();
        toCommit.put("comment", UUID.randomUUID().toString());
        toCommit.put("targets", new Long[]{request.getId()});
        toCommit.put("type", "decline");

        mockMvc.perform(putWeixin("/manage/data/agentRequest/pending").session(session)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(toCommit))
        )
                .andExpect(status().isOk());
        //
        mockMvc.perform(getWeixin("/manage/data/agentRequest/pending").session(session)
                .param("search", name)
                .param("offset", "0")
                .param("limit", "10")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows.length()").value(0));

        // 接受 然后就是合伙人了
        request = agentService.newRequest(user, name, randomMobile());


        toCommit.put("targets", new Long[]{request.getId()});
        toCommit.put("type", "approve");

        mockMvc.perform(putWeixin("/manage/data/agentRequest/pending").session(session)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsBytes(toCommit))
        )
                .andExpect(status().isOk());

        mockMvc.perform(getWeixin("/manage/data/agentRequest/pending").session(session)
                .param("search", name)
                .param("offset", "0")
                .param("limit", "10")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows.length()").value(0));

        assertThat(userService.by(user.getId()).getAgentInfo())
                .isNotNull();


    }


}