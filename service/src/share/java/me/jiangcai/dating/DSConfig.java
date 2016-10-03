package me.jiangcai.dating;

import me.jiangcai.lib.test.config.H2DataSourceConfig;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author CJ
 */
public class DSConfig extends H2DataSourceConfig {
    @Bean
    public DataSource dataSource() throws IOException {
        return dataSource("dating");
    }
}
