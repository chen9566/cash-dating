package me.jiangcai.dating;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CJ
 */
public class Mb4Test extends ServiceBaseTest {


    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
//    @Rollback(false)
    public void mb4() {
        String mb4 = "walmart obama \uD83D\uDC7D\uD83D\uDC94";
        User user = new User();
        user.setNickname(mb4);
        user = userRepository.save(user);
        System.out.println(user.getNickname());

        for (User user1 : userRepository.findAll()) {
            System.out.println(user1.getNickname());
        }
    }

}
