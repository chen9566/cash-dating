package me.jiangcai.dating.web.advice;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;

/**
 * @author CJ
 */
@Component
public class TRJNotifyLocker implements HandlerInterceptor {
    private static final Log log = LogFactory.getLog(TRJNotifyLocker.class);

    private final String code;

    @Autowired
    public TRJNotifyLocker(Environment environment) {
        this.code = environment.getProperty("dating.trj.code", "Einstein");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String signed = Hex
                .encodeHexString(messageDigest.digest((request.getRequestURL().toString() + code)
                        .getBytes("UTF-8")));
        final String header = request.getHeader("Kuanye_Auth");
        log.debug("Excepted:" + signed + " Header:" + header);
        if (signed.equalsIgnoreCase(header))
            return true;
        response.sendError(403, "Bad Auth");
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
