package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.AgentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.editor)
public class ManageAgentControllerTest extends ManageWebTest {

    @Autowired
    private AgentService agentService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void index() {
        driver.get("http://localhost/manage/agent");
//        System.out.println(driver.getPageSource());
        assertThat(driver.getTitle())
                .isEqualTo("合伙人");
    }

    // 随便搞一个用户 再展示下
    @Test
    public void data() throws Exception {

        final String openId = UUID.randomUUID().toString().replaceAll("-", "");
        User newUser = userService.newUser(openId, null);
        newUser.setNickname(UUID.randomUUID().toString());
        userRepository.save(newUser);

        MockHttpSession session = mvcLogin();
        mockMvc.perform(getWeixin("/manage/data/agent").session(session)
                .param("search", newUser.getNickname())
                .param("offset", "0")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(similarDataJsonAs("/mock/agent.json"));
    }

}