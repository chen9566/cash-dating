package me.jiangai.dating;

import me.jiangai.dating.web.WebConfig;
import me.jiangcai.lib.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebConfig.class})
public abstract class WebTest extends SpringWebTest {

}
