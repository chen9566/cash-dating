package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.manager)
public class ManageControllerTest extends ManageWebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void index() {
        System.out.println(driver.getPageSource());
    }

    @Test
    public void grant() throws Exception {
        // mockMvc
        MockHttpSession session = mvcLogin();

        User user = userService.newUser(UUID.randomUUID().toString().replaceAll("-", ""), null);

        ManageStatus manageStatus = ManageStatus.values()[random.nextInt(ManageStatus.values().length - 1)];
        mockMvc.perform(putWeixin("/manage/grant/{0}", String.valueOf(user.getId()))
                .content(manageStatus.name())
                .session(session))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(userService.by(user.getId()).getManageStatus())
                .isEqualByComparingTo(manageStatus);

        //但不能改成root
        mockMvc.perform(putWeixin("/manage/grant/{0}", String.valueOf(user.getId()))
                .content("root")
                .session(session))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(userService.by(user.getId()).getManageStatus())
                .isEqualByComparingTo(manageStatus);

        // root 也无法被更新
        user = userService.by(user.getId());
        user.setManageStatus(ManageStatus.root);
        userRepository.save(user);

        mockMvc.perform(putWeixin("/manage/grant/{0}", String.valueOf(user.getId()))
                .content(manageStatus.name())
                .session(session))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(userService.by(user.getId()).getManageStatus())
                .isEqualByComparingTo(ManageStatus.root);
    }

}