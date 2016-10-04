package me.jiangcai.dating.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 目前设计2个入口
 * 1 收款 既首页
 * 2 我的
 *
 * @author CJ
 */
@Controller
public class HomeController {

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    public String index() {
        return "receivables.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = {"/my"})
    public String my() {
        return "my.html";
    }


}