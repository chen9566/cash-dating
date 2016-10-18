package me.jiangcai.dating.entity;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.support.BookRateLevel;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class UserAgentInfoTest extends ServiceBaseTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    public void well() {
        WeixinUserDetail detail = createNewUser();

        final User user = userService.byOpenId(detail.getOpenId());
//        System.out.println(user.getMyAgentInfo());

        user.setMyAgentInfo(user.updateMyAgentInfo());

        user.getMyAgentInfo().setBookLevel(BookRateLevel.values()[random.nextInt(BookRateLevel.values().length)]);

        System.out.println(user.getMyAgentInfo().getBookLevel().toRate());

        for (BookRateLevel level : BookRateLevel.values()) {
            System.out.println(level.toRate());
        }

        userRepository.save(user);
    }

}