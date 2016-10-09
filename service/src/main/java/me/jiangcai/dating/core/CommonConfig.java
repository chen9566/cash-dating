package me.jiangcai.dating.core;

import me.jiangcai.dating.Version;
import me.jiangcai.dating.entity.SystemString;
import me.jiangcai.dating.repository.SystemStringRepository;
import me.jiangcai.lib.bracket.BracketSpringConfig;
import me.jiangcai.lib.jdbc.JdbcSpringConfig;
import me.jiangcai.lib.notice.NoticeSpringConfig;
import me.jiangcai.lib.resource.ResourceSpringConfig;
import me.jiangcai.lib.spring.logging.LoggingConfig;
import me.jiangcai.lib.upgrade.UpgradeSpringConfig;
import me.jiangcai.lib.upgrade.VersionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 核心服务 加载者
 *
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.loveport.service")
@Import({BracketSpringConfig.class, ResourceSpringConfig.class, UpgradeSpringConfig.class, JdbcSpringConfig.class
        , NoticeSpringConfig.class, LoggingConfig.class})
@EnableJpaRepositories(basePackages = {"me.jiangcai.loveport.repository"})
class CommonConfig {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SystemStringRepository systemStringRepository;

    @Bean
    @SuppressWarnings("unchecked")
    public VersionInfoService versionInfoService() {
        final String versionKey = "version.database";
        return new VersionInfoService() {

            @Override
            public <T extends Enum> T currentVersion(Class<T> type) {
                SystemString systemString = systemStringRepository.findOne(versionKey);
                if (systemString == null)
                    return null;
                return (T) Version.valueOf(systemString.getValue());
            }

            @Override
            public <T extends Enum> void updateVersion(T currentVersion) {
                SystemString systemString = systemStringRepository.findOne(versionKey);
                if (systemString == null) {
                    systemString = new SystemString();
                    systemString.setId(versionKey);
                }
                systemString.setValue(currentVersion.name());
                systemStringRepository.save(systemString);
            }
        };
    }


}
