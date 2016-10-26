package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 有人向平台用户支付,特点是所有人都可创建。
 * 扫码(GET)以后直接创建,并且展示付款二维码;
 * 这里可以简单要求openId以保证用户体验
 *
 * @author CJ
 */
@Entity
@Setter
@Getter
public class PayToUserOrder extends CashOrder {

    /**
     * 由谁创建
     */
    @ManyToOne
    private User from;
    // 会留下只言片语么?

}
