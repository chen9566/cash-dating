package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.support.ManageStatus;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@AsManage(ManageStatus.root)
public class ManageScriptControllerTest extends ManageWebTest {

    @Test
    public void index() {
        driver.get("http://localhost/manage/script");
        assertThat(driver.getTitle())
                .isEqualTo("执行脚本");
    }

    @Test
    public void run() throws Exception {
        MockHttpSession session = mvcLogin();
        runScript(session, "'中文'");
        runScript(session, "applicationContext");
        runScript(session, "applicationContext.getBean('systemService')");
        runScript(session, "applicationContext.getBean('systemService')");
//        runScript(session, "applicationContext.getBean('systemService').updateSystemString('dating.rate.preferential','0.99')");
//        runScript(session, "applicationContext.getBean('systemService').systemPreferentialRate()");
    }

    private void runScript(MockHttpSession session, String script) throws Exception {
        String response = mockMvc.perform(putWeixin("/manage/execScript")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE + " ;charset=UTF-8")
                .contentType(MediaType.TEXT_PLAIN_VALUE + " ;charset=UTF-8")
//                .param("script", script)
                .content(script)
                .session(session))
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

}