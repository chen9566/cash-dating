package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 代理商相关
 *
 * @author CJ
 */
@Controller
public class AgentController {

    @Autowired
    private AgentService agentService;

    /**
     * 总的入口,如果还不是代理商 应该去代理商申请页
     */
    @RequestMapping(method = RequestMethod.GET, value = "/agent")
    public String index(@AuthenticationPrincipal User user) {
        if (user.getAgentInfo() == null) {
            return "agent.html";
        }
        throw new NoSuchMethodError("what about agent?");
    }

    /**
     * 申请成为代理商
     */
    @RequestMapping(method = RequestMethod.POST, value = "/agent")
    @ResponseStatus(HttpStatus.CREATED)
    public void request(@AuthenticationPrincipal User user, String name, String mobile) {
        if (StringUtils.isEmpty(name))
            throw new IllegalArgumentException("name is required");
        if (StringUtils.isEmpty(mobile))
            throw new IllegalArgumentException("mobile is required");
        if (mobile.length() < 11)
            throw new IllegalArgumentException("mobile is required");

        agentService.newRequest(user, name, mobile);
    }

}
