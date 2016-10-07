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

//    @Autowired
//    private Environment environment;
//
//    @Bean
//    public PublicAccountSupplier publicAccountSupplier() {
//        PublicAccount account = new PublicAccount();
//        account.setAppID(environment.getRequiredProperty("cash.weixin.appId"));
//        account.setAppSecret(environment.getRequiredProperty("cash.weixin.appSecret"));
//        account.setInterfaceURL(environment.getRequiredProperty("cash.weixin.url"));
//        account.setInterfaceToken(environment.getRequiredProperty("cash.weixin.token"));
//        return new SinglePublicAccountSupplier(account);
//    }

//    @PostConstruct
//    @Autowired
//    public void init(PublicAccountSupplier supplier, WeixinService weixinService) throws IOException {
//        // 在公众号建立菜单
//        String json = environment.getProperty("cash.weixin.menus");
//        if (!StringUtils.isEmpty(json)) {
//            weixinService.menus(json, supplier.findByHost(null));
//        }
//
//    }
}
