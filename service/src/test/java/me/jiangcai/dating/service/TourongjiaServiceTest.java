package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Loan;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class TourongjiaServiceTest extends ServiceBaseTest {

    @Autowired
    private TourongjiaService tourongjiaService;

    @Test
    public void recommend() throws Exception {
//        tourongjiaService.recommend();
        Loan[] loanList = tourongjiaService.loanList();
        System.out.println(Arrays.toString(loanList));
        assertThat(loanList)
                .isNotEmpty();

        User user = userService.byOpenId(createNewUser().getOpenId());

        Loan loan = Stream.of(loanList).findAny().orElse(null);

        String id = tourongjiaService.loan(loan, loan.getTerm()[random.nextInt(loan.getTerm().length)], user, "轮渡人"
                , new BigDecimal("5000"), "110000", "110103", "很复杂");
        assertThat(id)
                .isNotEmpty();

        String status = tourongjiaService.checkLoanStatus(id);
        assertThat(status)
                .isNotEmpty();
        System.out.println(status);
    }

}