package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.model.trj.Loan;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

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
    }

}