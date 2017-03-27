/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package me.jiangcai.dating;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * 用于本地升级测试
 *
 * @author CJ
 */
class LocalMysqlDS {
    @Bean
    public DataSource dataSource() throws IOException {
        //
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setDatabaseName("cash");
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setUser("root");
        return dataSource;
    }
}
