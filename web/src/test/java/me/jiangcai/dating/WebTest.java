package me.jiangcai.dating;

import com.gargoylesoftware.htmlunit.WebClient;
import me.jiangcai.dating.web.WebConfig;
import me.jiangcai.lib.test.SpringWebTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.WebConnectionHtmlUnitDriver;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * @author CJ
 */
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebConfig.class})
public abstract class WebTest extends SpringWebTest {


    protected MockHttpServletRequestBuilder getWeixin(String urlTemplate, Object... urlVariables) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate, urlVariables);
        builder.header("user-agent", "MicroMessenger");
        return builder;
    }


    @Override
    protected void createWebDriver() {
        driver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(mockMvc)
//                .useMockMvcForHosts("wxtest.jiangcai.me")
                .withDelegate(new WebConnectionHtmlUnitDriver() {
                    @Override
                    protected WebClient modifyWebClientInternal(WebClient webClient) {
                        webClient.addRequestHeader("user-agent", "MicroMessenger");
                        return super.modifyWebClientInternal(webClient);
                    }
                })
                // DIY by interface.
                .build();
    }


}
