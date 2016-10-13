package me.jiangcai.dating.web.controller.pay;

import me.jiangcai.dating.WebTest;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.repository.CashOrderRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author CJ
 */
public class PayControllerTest extends WebTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CashOrderRepository cashOrderRepository;

    @Test
    @Transactional
    public void orderStatus() throws Exception {
        // 所有人都可以检查
        CashOrder cashOrder = new CashOrder();
        cashOrder.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        cashOrder = cashOrderRepository.save(cashOrder);

        mockMvc.perform(get("/orderCompleted/{1}", cashOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));

        cashOrder.setCompleted(true);
        mockMvc.perform(get("/orderCompleted/{1}", cashOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

}