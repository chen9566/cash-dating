package me.jiangcai.dating.service;

import org.junit.Ignore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 测试下生产环境
 *
 * @author CJ
 */
//@ContextConfiguration(classes = ProductTourongjiaServiceTest.Config.class)
@Ignore
public class ProductTourongjiaServiceTest extends TourongjiaServiceTest {

    @Configuration
    @PropertySource("classpath:/trj.properties")
    static class Config {
    }


}
