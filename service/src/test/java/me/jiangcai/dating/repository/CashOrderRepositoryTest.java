package me.jiangcai.dating.repository;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.OrderService;
import me.jiangcai.wx.model.WeixinUserDetail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class CashOrderRepositoryTest extends ServiceBaseTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;
    @Autowired
    private OrderService orderService;


    @Test
    public void findOrderFlow() throws Exception {
        WeixinUserDetail detail = createNewUser();

        User user = userService.byOpenId(detail.getOpenId());
        CashOrder cashOrder1 = orderService.newOrder(user, new BigDecimal("100"), UUID.randomUUID().toString()
                , user.getCards().get(0).getId());
//        cashOrder1.setCompleted(true);
        cashOrderRepository.save(cashOrder1);

        CashOrder cashOrder2 = orderService.newOrder(user, new BigDecimal("200"), UUID.randomUUID().toString()
                , user.getCards().get(0).getId());
//        cashOrder2.setCompleted(true);
        cashOrder2.setStartTime(LocalDateTime.now().plusMinutes(2));
        cashOrderRepository.save(cashOrder2);

        List list = cashOrderRepository.findOrderFlow(user);
        System.out.println(list);
        assertThat(list)
                .isEmpty();

        cashOrder1.setCompleted(true);
        cashOrderRepository.save(cashOrder1);

        list = cashOrderRepository.findOrderFlow(user);
        assertThat(list)
                .hasSize(1);
        assertOrder(list.get(0), cashOrder1);

        cashOrder2.setCompleted(true);
        cashOrderRepository.save(cashOrder2);

        list = cashOrderRepository.findOrderFlow(user);

        assertThat(list)
                .hasSize(2);
        assertOrder(list.get(0), cashOrder2);
        assertOrder(list.get(1), cashOrder1);


    }

    private void assertOrder(Object object, CashOrder order) {
        Object[] objects = (Object[]) object;
        assertThat(objects[0])
                .isEqualTo(order);
    }

}