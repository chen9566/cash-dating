package me.jiangcai.dating.entity.sale;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.sale.support.FakeCategory;
import me.jiangcai.goods.GoodsImage;
import me.jiangcai.goods.Seller;
import me.jiangcai.goods.TradeEntity;
import me.jiangcai.goods.core.entity.SimpleGoodsImage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 伪装的商品
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class FakeGoods extends CashGoods {

    private FakeCategory fakeCategory;
    /**
     * 销量，可以随意修改
     */
    private long sales;
    /**
     * 库存，可以随意修改
     */
    private long stock;
    /**
     * 显示折扣
     */
    @Column(length = 7)
    private String discount;

    @Override
    public boolean isTicketGoods() {
        return false;
    }

    @Override
    protected void moreModel(Map<String, Object> data) {
        data.put("sales", sales);
        data.put("stock", stock);
        data.put("fakeCategory", fakeCategory == null ? null : fakeCategory.name());
        data.put("discount", discount);
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

    /**
     * @return 获取新品图片
     */
    public GoodsImage getFreshlyGoodsImage() {
        final List<SimpleGoodsImage> goodsImages = getGoodsImages();
        if (goodsImages == null)
            return getTitleGoodsImage();
        return goodsImages.stream()
                .filter(this::isFreshlyGoodsImage)
                .map(simpleGoodsImage -> (GoodsImage) simpleGoodsImage)
                .findFirst()
                .orElse(getTitleGoodsImage());
    }

    private boolean isFreshlyGoodsImage(SimpleGoodsImage simpleGoodsImage) {
        return simpleGoodsImage.getDescription().contains("新品图片");
    }

    public List<SimpleGoodsImage> getNormalGoodsImages() {
        if (getGoodsImages() == null)
            return Collections.emptyList();
        return getGoodsImages().stream()
                .filter(simpleGoodsImage -> !isFreshlyGoodsImage(simpleGoodsImage))
                .collect(Collectors.toList());
    }


}
