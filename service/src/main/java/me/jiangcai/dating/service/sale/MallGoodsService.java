package me.jiangcai.dating.service.sale;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.entity.sale.TicketBatch;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.model.TicketInfo;
import me.jiangcai.goods.Goods;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 商品管理
 *
 * @author CJ
 */
public interface MallGoodsService {

    /**
     * @return 所有在售商品
     */
    @Transactional(readOnly = true)
    List<CashGoods> saleGoods();

    /**
     * 获得卡券类商品信息
     *
     * @param goods 必须是 {@link TicketGoods}
     * @return info
     */
    @Transactional(readOnly = true)
    TicketInfo ticketInfo(CashGoods goods);

    @Transactional
    Goods saveGoods(Goods goods);

    @Transactional(readOnly = true)
    CashGoods findGoods(long id);

    /**
     * 添加卡券类商品
     *
     * @param brand       品牌
     * @param stockStyle  库存风格
     * @param name        名字
     * @param price       售价
     * @param subPrice    副标题
     * @param description 描述
     * @param notes       购买须知HTML
     * @param detail      富文本HTML
     * @param imagePaths  相关图片
     * @return 已添加的商品
     */
    @Transactional
//    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "')")
    TicketGoods addTicketGoods(String brand, String stockStyle, String name, BigDecimal price, String subPrice, String description
            , String notes, String detail, String... imagePaths) throws IOException;

    /**
     * 建立订单咯
     *
     * @param user  用户
     * @param goods 商品
     * @param count 数量
     * @return 订单
     */
    @Transactional
    CashTrade createOrder(User user, CashGoods goods, int count);

    /**
     * 上传卡券类商品的卡券
     *
     * @param user        上传者
     * @param goods       相关商品
     * @param expiredDate 过期时间
     * @param comment     备注
     * @param codes       所有可用的卡券
     * @return 批次
     */
    @Transactional
    TicketBatch addTicketBatch(User user, TicketGoods goods, LocalDate expiredDate, String comment
            , Iterable<String> codes);
}
