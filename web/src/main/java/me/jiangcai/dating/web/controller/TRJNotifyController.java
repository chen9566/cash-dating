package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.service.WealthService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/trj/notify")
public class TRJNotifyController {

    private static final Log log = LogFactory.getLog(TRJNotifyController.class);

    @Autowired
    private WealthService wealthService;

    @RequestMapping(method = RequestMethod.PUT, value = "/ItemLoan/{id}/reject")
    @ResponseBody
    @Transactional
    public boolean reject(@PathVariable("id") String id, @RequestBody(required = false) String comment) {
        wealthService.supplierRejectLoan(id, comment);
        return true;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/ItemLoan/{id}/accept", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional
    public boolean accept(@PathVariable("id") String id, @RequestBody Map<String, Object> data) {
        if (log.isDebugEnabled()) {
            log.debug("[TRJ Listener]comment:" + data.get("comment"));
            log.debug("[TRJ Listener]amount:" + data.get("amount"));
        }
        wealthService.supplierAcceptLoan(id, (String) data.get("comment")
                , new BigDecimal(((Number) data.get("amount")).doubleValue()));
        return true;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/ItemLoan/{id}/failed")
    @ResponseBody
    @Transactional
    public boolean failed(@PathVariable("id") String id, @RequestBody(required = false) String comment) {
        wealthService.supplierFailedLoan(id, comment);
        return true;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/ItemLoan/{id}/success")
    @ResponseBody
    @Transactional
    public boolean success(@PathVariable("id") String id, @RequestBody(required = false) String comment) {
        wealthService.supplierSuccessLoan(id, comment);
        return true;
    }

}
