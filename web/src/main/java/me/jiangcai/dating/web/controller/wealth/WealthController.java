package me.jiangcai.dating.web.controller.wealth;

import me.jiangcai.dating.entity.LoanRequest;
import me.jiangcai.dating.entity.ProjectLoanRequest;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.entity.support.LoanRequestStatus;
import me.jiangcai.dating.model.trj.Financing;
import me.jiangcai.dating.model.trj.Loan;
import me.jiangcai.dating.model.trj.ProjectLoan;
import me.jiangcai.dating.model.trj.VerifyCodeSentException;
import me.jiangcai.dating.repository.LoanRequestRepository;
import me.jiangcai.dating.service.PayResourceService;
import me.jiangcai.dating.service.SystemService;
import me.jiangcai.dating.service.TourongjiaService;
import me.jiangcai.dating.service.UserService;
import me.jiangcai.dating.service.WealthService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

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
    @Autowired
    private SystemService systemService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private LoanRequestRepository loanRequestRepository;

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
    @Transactional(readOnly = true)
    public ResponseEntity<?> financingUrl(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> data) {
        user = userService.by(user.getId());
        String id = (String) data.get("id");
        assert (id != null);
        String code = (String) data.get("code");
        if (code != null) {
            try {
                tourongjiaService.bind(user.getMobileNumber(), code);
            } catch (Throwable ex) {
                log.debug("mandalay", ex);
                return ResponseEntity.badRequest()
                        .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                        .body(ex.getMessage());
            }
        }

        try {
            return ResponseEntity.ok(wealthService.financingUrl(user, id).toString());
        } catch (VerifyCodeSentException ex) {
            log.debug(user.getMobileNumber(), ex);
            return null;
        } catch (Throwable ex) {
            log.debug("mandalay", ex);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.valueOf(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                    .body(ex.getMessage());
        }
    }

    ////////////// 借款

    @RequestMapping(method = RequestMethod.PUT, value = "/projectLoanContract")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void projectLoanContract(@RequestBody Map<String, Object> data) throws IOException {
        // 此处签约
        // requestId contract
        long requestId = ((Number) data.get("requestId")).longValue();
        String contract = (String) data.get("contract");
        final ProjectLoanRequest loanRequest = (ProjectLoanRequest) loanRequestRepository.getOne(requestId);

        if (StringUtils.isEmpty(loanRequest.getSupplierRequestId()))
            throw new IllegalStateException("have no supplier id");
        if (!StringUtils.isEmpty(loanRequest.getContracts().get(contract)))
            return;
        String id = tourongjiaService.signContract(loanRequest.getSupplierRequestId(), contract);

        loanRequest.getContracts().put(contract, id);
    }

    /**
     * 项目贷款成功以后的展示页面
     *
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/projectLoan")
    @Transactional(readOnly = true)
    public String projectLoanSuccess(@AuthenticationPrincipal User user, long id, Model model) {
        final LoanRequest loanRequest = loanRequestRepository.getOne(id);
        if (loanRequest.getProcessStatus() != LoanRequestStatus.contract)
            throw new IllegalStateException("not accept project loan");
        if (!(loanRequest instanceof ProjectLoanRequest))
            throw new IllegalStateException("ProjectLoanRequest only");
        if (!loanRequest.getLoanData().getOwner().equals(user))
            throw new AccessDeniedException("");
        model.addAttribute("request", loanRequest);
        return "loansuccess.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loan")
    public String loan(Model model) throws IOException {
        model.addAttribute("loanList", wealthService.loanList());
        return "loanmain.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanStart")
    public String loanStart(Model model, @RequestParam String id) throws IOException {
        Loan loan = putLoanAsProject(model, id);
        if (loan instanceof ProjectLoan) {
            model.addAttribute("creditLimitYears", systemService.getProjectLoanCreditLimit());
            model.addAttribute("loanTermDays", wealthService.nextProjectLoanTerm());
            return "itemloan.html";
        }
        return "loan.html";
    }

    private Loan putLoanAsProject(Model model, String id) throws IOException {
        final Loan loan = loanInstance(id);
        model.addAttribute("project", loan);
        return loan;
    }

    private Loan loanInstance(String id) throws IOException {
        return Stream.of(wealthService.loanList())
                .filter(loan -> loan.getProductId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loanSummary")
    public String loanSummary(Model model, String id, String amount, String period) throws IOException {
        Loan loan = putLoanAsProject(model, id);
        model.addAttribute("amount", amount);
        model.addAttribute("period", period);
        if (loan instanceof ProjectLoan)
            return "itempersonalup.html";
        return "personalup.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loanProjectSubmit")
    public String loanProjectSubmit(@AuthenticationPrincipal User user, String id, BigDecimal amount, String name
            , String number
            , @RequestParam("province") String provinceCode, @RequestParam("city") String cityCode
            , String homeAddress, String employer
            , int personalIncome, int familyIncome, int age
            , boolean hasHouse
            , Model model) throws IOException {

        Address address = new Address();
        address.setProvince(PayResourceService.provinceById(provinceCode));
        address.setCity(PayResourceService.cityById(cityCode));

        final Loan loan = loanInstance(id);
        assert loan instanceof ProjectLoan;
        ProjectLoan projectLoan = (ProjectLoan) loan;
        LoanRequest loanRequest = wealthService.loanRequest(user.getOpenId(), projectLoan, null, amount
                , name, number, address, homeAddress, employer, personalIncome, familyIncome, age, hasHouse);

        model.addAttribute("loanRequest", loanRequest);
        return "id.html";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loanSubmit")
    public String loanSubmit(@AuthenticationPrincipal User user, String id, BigDecimal amount, int period, String name
            , String number
            , @RequestParam("province") String provinceCode, @RequestParam("city") String cityCode
            , Model model) throws IOException {

        Address address = new Address();
        address.setProvince(PayResourceService.provinceById(provinceCode));
        address.setCity(PayResourceService.cityById(cityCode));

        final Loan loan = loanInstance(id);
        LoanRequest loanRequest = wealthService.loanRequest(user.getOpenId(), loan, amount, period, null
                , name, number, address);
        assert !(loan instanceof ProjectLoan);
        wealthService.submitLoanRequest(loanRequest.getId());
        return "redirect:/loanComplete";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanID2")
    @Transactional
    public String loanID(long loanRequestId, String path) throws IOException {
        // 这个时候应该去身份证那边
        wealthService.updateLoanIDImages(loanRequestId, null, null, path);
        wealthService.submitLoanRequest(loanRequestId);
        return "personalok.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanID")
    public String loanID(long loanRequestId, String backId, String frontId, Model model) throws IOException {
        // 这个时候应该去身份证那边
        wealthService.updateLoanIDImages(loanRequestId, backId, frontId, null);
        model.addAttribute("loanRequest", loanRequestRepository.getOne(loanRequestId));
//        return "redirect:/card?nextAction=/loanCard/" + loanRequestId + "&workModel=loan";
        return "handid.html";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanCard/{loanRequestId}")
    public String loanCard(@PathVariable("loanRequestId") long loanRequestId, long cardId) {
        wealthService.updateLoanCard(loanRequestId, cardId);
        wealthService.submitLoanRequest(loanRequestId);
        return "redirect:/loanComplete";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/loanComplete")
    public String loanCard() {
        return "personalok.html";
    }

}
