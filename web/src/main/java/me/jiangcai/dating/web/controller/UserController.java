package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.web.controller.support.DataController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

/**
 * @author CJ
 */
@Controller
@RequestMapping(value = "/manage/data/user")
public class UserController extends DataController<User> {

    @Override
    protected Class<User> type() {
        return User.class;
    }

    @Override
    protected List<DataField> fieldList() {
        return Arrays.asList(new StringField("nickname"));
    }
}
