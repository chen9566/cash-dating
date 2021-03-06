package me.jiangcai.dating;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author CJ
 */
public class DSConfig {

    @Bean
//    @DependsOn("h2Server")
    public DataSource dataSource() throws IOException {
        //
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        //        dataSource.setUrl("jdbc:h2:target/cash");
        dataSource.setUrl("jdbc:h2:mem:cash;DB_CLOSE_DELAY=-1");//all  3 31; apart : 9s
        return dataSource;
    }
}
