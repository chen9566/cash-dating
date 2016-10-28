package me.jiangcai.dating.service;

import me.jiangcai.dating.ServiceBaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CJ
 */
public class TourongjiaServiceTest extends ServiceBaseTest {

    @Autowired
    private TourongjiaService tourongjiaService;

    @Test
    public void recommend() throws Exception {
        tourongjiaService.recommend();
    }

}