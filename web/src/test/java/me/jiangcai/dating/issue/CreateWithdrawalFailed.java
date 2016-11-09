package me.jiangcai.dating.issue;

import me.jiangcai.chanpay.exception.ServiceException;
import me.jiangcai.dating.LoginWebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.mock.MockTransactionService;
import me.jiangcai.dating.repository.CashOrderRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 成功支付,但是建立支付订单时错误,导致已支付的信息丢失。
 *
 * @author CJ
 */
public class CreateWithdrawalFailed extends LoginWebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;

    @Test
    public void work() throws IOException, SignatureException {
        User user = currentUser();
        CashOrder withdrawalFailedOrder = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , user.getCards().get(0).getId());
        changeTime(withdrawalFailedOrder, LocalDateTime.now().plusHours(-2));
        // 要校验这个事情 需要先设定创建支付订单必然失败
        MockTransactionService.FailedServiceCardNumber = cardService.recommend(withdrawalFailedOrder).getNumber();

        try {
            tradeSuccess(withdrawalFailedOrder);
        } catch (ServiceException ex) {
            System.out.println(ex.toString());
        }
        MockTransactionService.FailedServiceCardNumber = null;

        //此时必须保证订单状态是成功的!
        assertThat(orderService.isComplete(withdrawalFailedOrder.getId()))
                .isTrue();
        CashOrder order = cashOrderRepository.getOne(withdrawalFailedOrder.getId());
        assertThat(order.getPlatformWithdrawalOrderSet())
                .isNullOrEmpty();
    }

}
