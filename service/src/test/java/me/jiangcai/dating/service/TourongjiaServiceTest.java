package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.MobileToken;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
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
    public void single() throws IOException, VerifyCodeSentException {
        Financing financing = tourongjiaService.recommend();
        System.out.println(financing);
        assertThat(financing)
                .isNotNull();
//        tourongjiaService.bind("18606509616", "1122");
//        URI url = tourongjiaService.financingURL(financing.getId(), "18606509616");
//        System.out.println(url);
    }

    /**
     * 理财
     *
     * @throws Exception
     */
    @Test
    public void financing() throws Exception {

        Financing financing = tourongjiaService.recommend();
        System.out.println(financing);
        assertThat(financing)
                .isNotNull();

        // abc123
        try {
            URI url = tourongjiaService.financingURL(financing.getId(), "18606509616");
            System.out.println(url);

            MobileToken token = tourongjiaService.token("18606509616");
            System.out.println(token);
            assertThat(token).isNotNull();
            // 097671

            financing = tourongjiaService.randomFinancing();
            System.out.println(financing);
            assertThat(financing)
                    .isNotNull();
        } catch (VerifyCodeSentException ex) {
//            tourongjiaService.bind("18606509616", "1122");
//            URI url = tourongjiaService.financingURL(financing.getId(), "18606509616");
//            System.out.println(url);
        }

    }

    //    @Repeat(20)
    @Test
    public void loan() throws Exception {
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

        Object status = tourongjiaService.checkLoanStatus(id);
        assertThat(status)
                .isNotNull();
        System.out.println(status);
    }

}