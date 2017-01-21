package me.jiangcai.dating.web.controller.manage.sale;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.dating.entity.sale.TicketGoods;
import me.jiangcai.dating.service.DataService;
import me.jiangcai.lib.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/manage/goods")
public class ManageSaleController {

    @Autowired
    private DataService data;
    @Autowired
    private ResourceService resourceService;

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "','" + Login.Role_Sale_Goods_Read_Value + "')")
    public String index() {
        return "manage/sale/goods.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/data", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Sale_Goods_Value + "','" + Login.Role_Sale_Goods_Read_Value + "')")
    @Transactional(readOnly = true)
    @ResponseBody
    public Object data(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return data.data(user, search, sort, order, offset, limit, CashGoods.class, fieldList(), null);
    }

    private List<DataField> fieldList() {
        return Arrays.asList(
                new DataService.NumberField("id", Long.class)
//                , new DataService.StringField("imageUrl") {
//                    @Override
//                    protected Expression<?> selectExpression(Root<?> root) {
//                        return root.get("goodsImages");
//                    }
//
//                    @Override
//                    public Object export(Object origin, MediaType type) {
//                        if (origin instanceof GoodsImage){
//                            return resourceService.getResource(((GoodsImage) origin).getDefaultImage().getResourcePath()).httpUrl();
//                        }
//
//                        if (origin instanceof List){
//                            GoodsImage goodsImage = (GoodsImage) ((List) origin).get(0);
//                            return resourceService.getResource(goodsImage.getDefaultImage().getResourcePath()).httpUrl().toString();
//                        }
//
//                        return origin.toString();
//                    }
//                }
                , new DataService.BooleanField("enable")
                , new DataService.StringField("brand")
                , new DataService.StringField("name")
                , new DataService.StringField("description")
                , new DataService.NumberField("price", BigDecimal.class)
                , new DataService.StringField("subPrice")
                , new DataService.NumberField("stock", Long.class) {
                    @Override
                    public Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
// 目前只支持 ticketGoods
//                        @SuppressWarnings("unchecked")
//                        Root<TicketGoods> ticketGoodsRoot = builder.treat((Root) root, TicketGoods.class);

                        return CashGoods.StockLeftExpression(builder, query, root);
                    }
                }, new DataService.StringField("type") {
                    @Override
                    protected Expression<?> selectExpression(Root<?> root) {
                        return root.type();
                    }

                    @Override
                    public Object export(Object origin, MediaType type) {
                        if (TicketGoods.class.equals(origin))
                            return "卡券类";
                        return "unknown";
                    }
                }
//                , new DataService.BooleanField("isTicket") {
//                    @Override
//                    public Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
//                        return builder.equal(root.type(), TicketGoods.class);
//                    }
//                }
        );
    }

}
