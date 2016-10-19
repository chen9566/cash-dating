package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.support.ManageStatus;
import org.junit.Test;

/**
 * @author CJ
 */
@AsManage(ManageStatus.general)
public class ManageControllerTest extends ManageWebTest {

    @Test
    public void index() {
        System.out.println(driver.getPageSource());
    }

    @Test
    public void grant() {
        // mockMvc
    }

}