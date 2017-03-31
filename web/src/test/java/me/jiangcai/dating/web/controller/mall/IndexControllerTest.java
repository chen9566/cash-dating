package me.jiangcai.dating.web.controller.mall;

import me.jiangcai.dating.entity.User;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class IndexControllerTest extends AbstractMallTest {

    /**
     * 公众号先行注册然后来到PC
     */
    @Test
    public void wxFirst() throws Exception {
        User user = helloNewUser(null, true);

        registerOnMall(user.getMobileNumber());
    }

    /**
     * PC先行注册然后再来到公众号
     */
    @Test
    public void pcFirst() throws Exception {

        String mobile = registerOnMall(null);

        User user = helloNewUser(null, true, mobile);

        assertThat(userService.by(user.getId()).getMobileNumber())
                .isEqualTo(mobile);
    }

}