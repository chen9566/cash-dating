package me.jiangcai.dating.web.controller.mall;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 允许非授权访问的假商城
 *
 * @author CJ
 */
@RequestMapping("/mall")
@Controller
public class IndexController {

    @RequestMapping(method = RequestMethod.GET, value = {"", "/"})
    public String home() {
        return "/mall/index.html";
    }

}
