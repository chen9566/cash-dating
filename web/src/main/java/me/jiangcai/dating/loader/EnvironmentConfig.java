package me.jiangcai.dating.loader;

import org.springframework.context.annotation.ImportResource;

/**
 * 服务器运行时所依赖的配置
 *
 * @author CJ
 */
@ImportResource({
        "classpath:datasource.xml"
})
// TODO 还公众号
public class EnvironmentConfig {
}
