package me.jiangcai.dating;

import me.jiangcai.lib.test.config.H2DataSourceConfig;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author CJ
 */
public class DSConfig extends H2DataSourceConfig {

    private static Server server;

    public DSConfig() throws SQLException {
        if (server == null) {
            server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
            server.start();
        }
    }

    @PreDestroy
    public void destroy() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroy();
    }

    //    @Bean(initMethod = "start", destroyMethod = "stop")
//    public Server h2Server() throws SQLException {
//        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
//    }

    @Bean
//    @DependsOn("h2Server")
    public DataSource dataSource() throws IOException {
        //
        final DriverManagerDataSource dataSource = (DriverManagerDataSource) dataSource("dating");
//        dataSource.setUrl("jdbc:h2:mem:dating");
        dataSource.setUrl("jdbc:h2:tcp://localhost:9092/~/cash");
        dataSource.setUsername("sa");
        return dataSource;
    }
}
