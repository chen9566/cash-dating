package me.jiangcai.dating.web.controller;

import me.jiangcai.dating.service.WealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CJ
 */
@Controller
@RequestMapping("/trj/notify")
public class TRJNotifyController implements HandlerInterceptor {

    @Autowired
    private WealthService wealthService;

    @RequestMapping(method = RequestMethod.PUT, value = "/ItemLoan/{id}/reject")
    @ResponseBody
    @Transactional
    public boolean reject(@PathVariable("id") String id, @RequestBody(required = false) String comment) {
        wealthService.supplierRejectLoan(id, comment);
        return true;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/ItemLoan/{id}/accept")
    @ResponseBody
    @Transactional
    public boolean accept(@PathVariable("id") String id, @RequestBody(required = false) String comment) {
        wealthService.supplierAcceptLoan(id, comment);
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
