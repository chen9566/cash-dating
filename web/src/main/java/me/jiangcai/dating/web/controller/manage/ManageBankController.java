package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Edit_Value + "')")
@Controller
public class ManageBankController {

    @Autowired
    private BankService bankService;

    @RequestMapping(method = RequestMethod.GET, value = "/manage/bank")
    public String index(Model model) {
        model.addAttribute("banks", bankService.list());
        return "manage/bank.html";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/manage/bank/{code}/background")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void change(@RequestBody String background, @PathVariable("code") String code) {
        bankService.updateBank(code, null, background);
    }
}
