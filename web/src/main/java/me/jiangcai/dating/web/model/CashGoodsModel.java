package me.jiangcai.dating.web.model;

import me.jiangcai.dating.entity.sale.CashGoods;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;

import java.util.Map;

/**
 * @author CJ
 */
public class CashGoodsModel extends CashGoods {
    @Override
    public boolean isTicketGoods() {
        return false;
    }

    @Override
    protected void moreModel(Map<String, Object> data) {

    }

    @Override
    public Seller getSeller() {
        return null;
    }

    @Override
    public void setSeller(Seller seller) {

    }

    @Override
    public TradeEntity getOwner() {
        return null;
    }

    @Override
    public void setOwner(TradeEntity owner) {

    }
}
