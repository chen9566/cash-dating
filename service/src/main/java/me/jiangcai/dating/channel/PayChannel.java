package me.jiangcai.dating.channel;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.model.PayMethod;

import java.io.IOException;
import java.security.SignatureException;

/**
 * @author CJ
 */
public interface PayChannel {

    /**
     * 建立一份实际支付订单
     *
     * @param order   系统订单
     * @param channel 渠道
     * @return 平台订单
     */
    PlatformOrder newOrder(CashOrder order, PayMethod channel) throws IOException, SignatureException;

    /**
     * 获取一个可以扫码支付的图片URL
     *
     * @param order 畅捷订单
     * @return 二维码
     * @throws IllegalStateException 如果这个订单已经无效或者根本没生成
     * @throws IOException           获取的过程发生问题
     */
    String QRCodeImageFromOrder(PlatformOrder order) throws IllegalStateException, IOException;
}
