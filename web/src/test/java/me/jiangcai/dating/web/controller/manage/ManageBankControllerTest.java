package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.service.BankService;
import org.junit.Test;
import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
@AsManage(ManageStatus.editor)
public class ManageBankControllerTest extends ManageWebTest {

    @Autowired
    private BankService bankService;

    @Test
    public void index() {
        driver.get("http://localhost/manage/bank");
        assertThat(driver.getTitle())
                .isEqualTo("银行管理");
        assertThat(driver.findElements(By.className("bank")))
                .isNotEmpty();
    }

    @Test
    public void change() throws Exception {
        MockHttpSession session = mvcLogin();
        // /manage/bank/2/background
        Bank bank = bankService.list().stream()
                .max(new RandomComparator())
                .orElse(null);
        String background = UUID.randomUUID().toString();
        mockMvc.perform(putWeixin("/manage/bank/{0}/background", bank.getCode()).session(session)
                .contentType(MediaType.TEXT_PLAIN)
                .content(background)
        )
                .andExpect(status().isNoContent());

        assertThat(bankService.byCode(bank.getCode()).getBackground())
                .isEqualTo(background);

        mockMvc.perform(putWeixin("/manage/bank/{0}/disabled", bank.getCode()).session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("true")
        )
                .andExpect(status().isNoContent());
        assertThat(bankService.list())
                .doesNotContain(bank);
        bankService.updateBank(bank.getCode(), null, null, false);
        assertThat(bankService.list())
                .contains(bank);
    }

}