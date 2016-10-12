package me.jiangcai.dating;

import me.jiangcai.dating.core.CoreConfig;
import me.jiangcai.lib.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, CoreConfig.class})
public abstract class ServiceBaseTest extends SpringWebTest {
}
