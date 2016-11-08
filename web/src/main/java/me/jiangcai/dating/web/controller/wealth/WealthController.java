package me.jiangcai.dating.web.controller.wealth;

import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.WealthService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
public class WealthController {

    private static final Log log = LogFactory.getLog(WealthController.class);
    @Autowired
    private WealthService wealthService;
    @Autowired
    private TourongjiaService tourongjiaService;
    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, value = "/financing")
    public String financing(Model model) throws IOException {
        final Financing financing = wealthService.currentFinancing();
        model.addAttribute("currentFinancing", financing);
        // 10.00 切割为
        String[] rates = financing.getYearRate().split("\\.");
        model.addAttribute("rate1", rates[0] + ".");
        model.addAttribute("rate2", rates[1] + "%");
        return "wealth.html";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/financingUrl")
    @ResponseBody
    @Transactional(readOnly = true)
    public String financingUrl(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> data)
            throws IOException {
        user = userService.by(user.getId());
        String id = (String) data.get("id");
        assert (id != null);
        String code = (String) data.get("code");
        if (code != null) {
            tourongjiaService.bind(user.getMobileNumber(), code);
        }

        try {
            return wealthService.financingUrl(user, id).toString();
        } catch (VerifyCodeSentException ex) {
            log.debug(user.getMobileNumber(), ex);
            return null;
        }
    }

}
