package me.jiangcai.dating;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author CJ
 */
public abstract class ManageWebTest extends LoginWebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Override
    public void forLogin() {
        super.forLogin();
        User user = currentUser();
        //AsManage
        AsManage asManage = AnnotationUtils.findAnnotation(getClass(), AsManage.class);
        assert asManage != null;

        user.setManageStatus(asManage.value());
        userRepository.save(user);

        driver.get("http://localhost/manage");
    }
}
