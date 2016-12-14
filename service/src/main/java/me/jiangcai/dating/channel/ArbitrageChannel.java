package me.jiangcai.dating.channel;

import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.PlatformOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.exception.ArbitrageBindFailedException;
import me.jiangcai.dating.model.PayChannel;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SignatureException;

/**
 * @author CJ
 */
public interface ArbitrageChannel {

    /**
     * @return 最低套现金额
     */
    int lowestAmount();

    /**
     * @return 手动获取套现结果
     */
    boolean arbitrageResultManually();

    /**
     * 检查套现结果
     *
     * @param order 付款订单
     * @throws IOException
     * @throws SignatureException
     */
    @Transactional
    void checkArbitrageResult(PlatformOrder order) throws IOException, SignatureException;

    /**
     * 检查支付结果
     *
     * @param order 付款订单
     * @return 是否已支付
     * @throws IOException
     * @throws SignatureException
     */
    boolean checkPayResult(PlatformOrder order) throws IOException, SignatureException;

    /**
     * 如果结果是true表示用户需要支付签预先绑定借记卡,也表示需要调用{@link #bindUser(User)}
     *
     * @return 是否使用一个订单同时作用于支付和提现
     */
    boolean useOneOrderForPayAndArbitrage();

    /**
     * 绑定用户的状态
     *
     * @param user 用户
     * @return 状态
     * @throws IOException
     * @throws SignatureException
     */
    void bindUser(User user) throws IOException, SignatureException;

    /**
     * 绑定用户的状态
     *
     * @param user 用户
     * @return 状态
     * @throws IOException
     * @throws SignatureException
     * @throws ArbitrageBindFailedException 如果绑定失败的话
     */
    ArbitrageAccountStatus bindingUserStatus(User user) throws IOException, SignatureException, ArbitrageBindFailedException;

    /**
     * 如果不可以管理则需要给予警告
     *
     * @return 是否可以管理旗下的借记卡
     */
    boolean debitCardManageable();

    /**
     * 建立一份实际支付订单
     *
     * @param order 系统订单
     * @param channel
     * @return 平台订单
     */
    PlatformOrder newOrder(CashOrder order, PayChannel channel) throws IOException, SignatureException;

    /**
     * 模拟套现的结果
     *
     * @param order   订单
     * @param success 是否成功
     * @param reason  理由
     */
    @Transactional
    void mockArbitrageResult(CashOrder order, boolean success, String reason);

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
