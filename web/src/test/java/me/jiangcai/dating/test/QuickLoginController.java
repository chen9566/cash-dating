package me.jiangcai.dating.test;

import me.jiangcai.dating.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author CJ
 */
@Controller
public class QuickLoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/quickLogin/{userId}")
    public String login(@PathVariable("userId") long userId, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        userService.loginAs(request, response, userService.by(userId));
        return null;
    }

}
