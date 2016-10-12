package me.jiangcai.dating.service;

import me.jiangcai.dating.Version;
import me.jiangcai.dating.entity.Bank;
import me.jiangcai.lib.jdbc.JdbcService;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import me.jiangcai.lib.upgrade.service.UpgradeService;
import me.jiangcai.wx.PublicAccountSupplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * 初始化服务,没有服务会依赖于它
 *
 * @author CJ
 */
@Service
public class InitService {

    private static final Log log = LogFactory.getLog(InitService.class);
    @Autowired
    public WeixinService weixinService;
    @Autowired
    private Environment environment;
    @Autowired
    private PublicAccountSupplier supplier;
    @Autowired
    private UpgradeService upgradeService;
    @Autowired
    private JdbcService jdbcService;

    @PostConstruct
    public void init() throws IOException {
        String json = environment.getProperty("cash.weixin.menus");
        log.debug(json);
        if (json != null)
            weixinService.menus(json, supplier.findByHost(null));


        upgradeService.systemUpgrade(new VersionUpgrade<Version>() {
            @Override
            public void upgradeToVersion(Version version) throws Exception {
                switch (version) {
                    case v102000:
                        jdbcService.tableAlterAddColumn(Bank.class, "weight", "50");
                        break;
                    case v101000:
                        System.out.println("update to 1.1?");
                        break;
                }
            }
        });
    }

}
