package me.jiangcai.dating.service;

import me.jiangcai.dating.Version;
import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserLoanData;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.lib.jdbc.ConnectionConsumer;
import me.jiangcai.lib.jdbc.ConnectionProvider;
import me.jiangcai.lib.jdbc.JdbcService;
import me.jiangcai.lib.resource.service.ResourceService;
import me.jiangcai.lib.upgrade.VersionUpgrade;
import me.jiangcai.lib.upgrade.service.UpgradeService;
import me.jiangcai.wx.PublicAccountSupplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 初始化服务,没有服务会依赖于它
 *
 * @author CJ
 */
@Service
public class InitService {

    private static final Log log = LogFactory.getLog(InitService.class);
    @Autowired
    private WeixinService weixinService;
    @Autowired
    private Environment environment;
    @Autowired
    private PublicAccountSupplier supplier;
    @Autowired
    private UpgradeService upgradeService;
    @Autowired
    private JdbcService jdbcService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ResourceService resourceService;

    @PostConstruct
    public void init() throws IOException, SQLException {

        // 初始化默认图片
        if (!resourceService.getResource(CashGoods.DefaultGoodsImagePath).exists()) {
            try (InputStream inputStream
                         = applicationContext.getResource("classpath:/" + CashGoods.DefaultGoodsImagePath)
                    .getInputStream()) {
                resourceService.uploadResource(CashGoods.DefaultGoodsImagePath, inputStream);
            }
        }

//        String json = environment.getProperty("cash.weixin.menus");
//        log.debug(json);
//        if (json != null)
//            weixinService.menus(json, supplier.findByHost(null));

        // 这里多了 1 是卡号 如果存在关系 还应该先解除关系
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }


        upgradeService.systemUpgrade(new VersionUpgrade<Version>() {
            @Override
            public void upgradeToVersion(Version version) throws Exception {
                switch (version) {
                    case v109001:
                        jdbcService.tableAlterAddColumn(ProjectLoanRequest.class, "mobileVerified", "0");
                        break;
                    case v108002:
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "hasHouse", "0");
                        break;
                    case v108011:
                        jdbcService.runStandaloneJdbcWork(new ConnectionConsumer() {
                            @Override
                            public void accept(ConnectionProvider connection) throws SQLException {
                                try (Statement statement = connection.getConnection().createStatement()) {
                                    statement.execute("ALTER TABLE `platformOrder` ADD FEE DECIMAL(20,2) DEFAULT 0");
                                }
                            }
                        });
                        jdbcService.tableAlterAddColumn(PlatformOrder.class, "platformId", null);
                        jdbcService.tableAlterAddColumn(Card.class, "ownerId", null);
                        break;
                    case v108001:
                        jdbcService.runStandaloneJdbcWork(new ConnectionConsumer() {
                            @Override
                            public void accept(ConnectionProvider connection) throws SQLException {
                                try (Statement statement = connection.getConnection().createStatement()) {
                                    statement.execute("ALTER TABLE loanRequest ADD DTYPE VARCHAR(31) NOT NULL DEFAULT 'LoanRequest';");
                                    // CashOrder
//                                    statement.executeUpdate("UPDATE cashorder SET DTYPE='CashOrder'");
                                }
                            }
                        });
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "backIdResource", null);
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "frontIdResource", null);
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "handIdResource", null);
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "homeAddress", null);
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "employer", null);
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "personalIncome", "0");
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "familyIncome", "0");
                        jdbcService.tableAlterAddColumn(UserLoanData.class, "age", "0");
                        break;
                    case v105001:
                        jdbcService.tableAlterAddColumn(UserOrder.class, "systemComment", null);
                        break;
                    case v105000:
                        jdbcService.tableAlterAddColumn(UserOrder.class, "withdrawalCompleted", "0");
                        jdbcService.runStandaloneJdbcWork(new ConnectionConsumer() {
                            @Override
                            public void accept(ConnectionProvider connection) throws SQLException {
                                try (Statement statement = connection.getConnection().createStatement()) {
                                    statement.execute("ALTER TABLE userorder ADD CARD_ID BIGINT(20) NULL");
                                    statement.executeUpdate("UPDATE userorder AS uo " +
                                            " INNER JOIN cashorder AS co ON co.id = uo.id" +
                                            " SET uo.WITHDRAWALCOMPLETED = co.WITHDRAWALCOMPLETED");
                                    statement.executeUpdate("UPDATE userorder AS uo " +
                                            " INNER JOIN cashorder AS co ON co.id = uo.id" +
                                            " SET uo.CARD_ID = co.CARD_ID");

                                    statement.execute("ALTER TABLE cashorder DROP CARD_ID");
                                    statement.execute("ALTER TABLE cashorder DROP WITHDRAWALCOMPLETED");
                                }
                            }
                        });

                        jdbcService.runStandaloneJdbcWork(new ConnectionConsumer() {
                            @Override
                            public void accept(ConnectionProvider connection) throws SQLException {
                                try (Statement statement = connection.getConnection().createStatement()) {
                                    statement.execute("ALTER TABLE `user` ADD SETTLEMENTREVENUE DECIMAL(20,2) DEFAULT 0");
                                    statement.execute("ALTER TABLE `user` ADD SETTLEMENTWITHDRAWAL DECIMAL(20,2) DEFAULT 0");
                                }
                            }
                        });
//                        jdbcService.tableAlterAddColumn(User.class, "settlementRevenue", "0");
//                        jdbcService.tableAlterAddColumn(User.class, "settlementWithdrawal", "0");
                        jdbcService.tableAlterAddColumn(Card.class, "disabled", "0");
                        jdbcService.tableAlterAddColumn(Bank.class, "disabled", "0");
//                        jdbcService.runStandaloneJdbcWork(new ConnectionConsumer() {
//                            @Override
//                            public void accept(ConnectionProvider connection) throws SQLException {
//                                try (Statement statement = connection.getConnection().createStatement()) {
//                                    statement.execute("ALTER TABLE cashorder ADD DTYPE VARCHAR(31) NULL");
//                                    // CashOrder
//                                    statement.executeUpdate("UPDATE cashorder SET DTYPE='CashOrder'");
//                                }
//                            }
//                        });
                        break;
                    case v103000:
                        jdbcService.tableAlterAddColumn(Bank.class, "background", "linear-gradient(to right, #E75C65 , #E8507D);");
                        jdbcService.runStandaloneJdbcWork(new ConnectionConsumer() {
                            @Override
                            public void accept(ConnectionProvider connection) throws SQLException {
                                try (Statement statement = connection.getConnection().createStatement()) {
                                    statement.execute("ALTER TABLE cashorder ADD CARD_ID BIGINT(20) NULL");
                                }
                            }
                        });
                        break;
                    case v102001:
                        jdbcService.tableAlterAddColumn(User.class, "enabled", "1");
                        break;
                    case v102000:
                        jdbcService.tableAlterAddColumn(Bank.class, "weight", "50");
                        break;
                    case v101000:
//                        System.out.println("update to 1.1?");
                        break;
                    case v30000:
                        jdbcService.tableAlterAddColumn(CashGoods.class, "weight", "0");
                        jdbcService.tableAlterAddColumn(CashGoods.class, "hot", "0");
                        jdbcService.tableAlterAddColumn(CashGoods.class, "freshly", "0");
                        jdbcService.tableAlterAddColumn(CashGoods.class, "special", "0");
                        jdbcService.tableAlterAddColumn(CashGoods.class, "createTime", null);
                        jdbcService.tableAlterAddColumn(CashGoods.class, "updateTime", null);
                        jdbcService.runStandaloneJdbcWork(connection -> {
                            try (Statement statement = connection.getConnection().createStatement()) {
                                statement.execute("UPDATE cashgoods SET `createTime`=current_timestamp,`updateTime`=current_timestamp");
                            }
                        });
                        break;
                }
            }
        });

        // 建立自定义函数
        try {
            jdbcService.runJdbcWork(connection -> {
                String sql;
                try {
                    if (connection.profile().isH2()) {
                        sql = StreamUtils.copyToString(applicationContext.getResource("classpath:/function.h2.sql").getInputStream(), Charset.forName("UTF-8"));
                    } else if (connection.profile().isMySQL()) {
                        sql = StreamUtils.copyToString(applicationContext.getResource("classpath:/function.mysql.sql").getInputStream(), Charset.forName("UTF-8"));
                    } else {
                        throw new IllegalStateException("not support " + connection.profile());
                    }

                    try (Statement statement = connection.getConnection().createStatement()) {
                        statement.executeUpdate(sql);
                    }

                } catch (IOException ex) {
                    throw new InternalError(ex);
                }

            });

        } catch (Exception ex) {
            log.warn("create function", ex);
        }

    }

}
