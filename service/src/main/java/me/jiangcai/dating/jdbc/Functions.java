package me.jiangcai.dating.jdbc;

import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author CJ
 */
@SuppressWarnings("unused")
public class Functions {

    /**
     * 根据商品字段获取库存量
     *
     * @param connection
     * @param goodsId
     * @return
     */
    public static long GoodsStock(Connection connection, long goodsId) throws IOException, SQLException {
        String sql = StreamUtils.copyToString(Functions.class.getResourceAsStream("/h2.GoodsStock.sql")
                , Charset.forName("UTF-8"));
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, goodsId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        }
    }

}
