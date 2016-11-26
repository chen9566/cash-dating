package me.jiangcai.dating.web.controller.wealth;

import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.service.PayResourceService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

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

    ///////////理财

    @RequestMapping(method = RequestMethod.GET, value = "/financing")
    public String financing(@AuthenticationPrincipal User user, Model model) throws IOException {
        user = userService.by(user.getId());
        if (tourongjiaService.token(user.getMobileNumber()).getBinding() == -1) {
            // 用户未注册 直接302
            return "redirect:" + tourongjiaService.loginURL(user.getMobileNumber()).toString();
        }

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

    ////////////// 借款
    @RequestMapping(method = RequestMethod.GET, value = "/loan")
    public String loan(Model model) throws IOException {
        model.addAttribute("loanList", wealthService.loanList());
        return "loanmain.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanStart")
    public String loanStart(Model model, @RequestParam String id) throws IOException {
        putLoanAsProject(model, id);
        return "loan.html";
    }

    private void putLoanAsProject(Model model, String id) throws IOException {
        model.addAttribute("project", loanInstance(id));
    }

    private Loan loanInstance(String id) throws IOException {
        return Stream.of(wealthService.loanList())
                .filter(loan -> loan.getProductId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loanSummary")
    public String loanSummary(Model model, String id, String amount, String period) throws IOException {
        putLoanAsProject(model, id);
        model.addAttribute("amount", amount);
        model.addAttribute("period", period);
        return "personalup.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loanSubmit")
    public String loanSubmit(@AuthenticationPrincipal User user, String id, BigDecimal amount, int period, String name
            , String number
            , @RequestParam("province") String provinceCode, @RequestParam("city") String cityCode
            , Model model) throws IOException {

        Address address = new Address();
        address.setProvince(PayResourceService.provinceById(provinceCode));
        address.setCity(PayResourceService.cityById(cityCode));

        LoanRequest loanRequest = wealthService.loanRequest(user.getOpenId(), loanInstance(id), amount, period, null
                , name, number, address);
        model.addAttribute("loanRequest", loanRequest);
        return "id.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanID")
    public String loanID(long loanRequestId, String backId, String frontId) {
        // 这个时候应该去身份证那边
        wealthService.updateLoanIDImages(loanRequestId, backId, frontId);
        return "redirect:/card?nextAction=/loanCard/" + loanRequestId + "&workModel=loan";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanCard/{loanRequestId}")
    public String loanCard(@PathVariable("loanRequestId") long loanRequestId, long cardId) {
        wealthService.updateLoanCard(loanRequestId, cardId);
        return "redirect:/loanComplete";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanComplete")
    public String loanCard() {
        return "personalok.html";
    }

}
