package me.jiangcai.dating.loader;

import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.classics.SinglePublicAccountSupplier;
import me.jiangcai.wx.model.PublicAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

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

    @Autowired
    private Environment environment;

    @Bean
    public PublicAccountSupplier publicAccountSupplier() {
        PublicAccount account = new PublicAccount();
        account.setAppID(environment.getRequiredProperty("cash.weixin.appId"));
        account.setAppSecret(environment.getRequiredProperty("cash.weixin.appSecret"));
        account.setInterfaceURL(environment.getRequiredProperty("cash.weixin.url"));
        account.setInterfaceToken(environment.getRequiredProperty("cash.weixin.token"));
        return new SinglePublicAccountSupplier(account);
    }

}
