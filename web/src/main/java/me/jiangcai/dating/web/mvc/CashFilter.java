package me.jiangcai.dating.web.mvc;

import org.springframework.util.NumberUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
public class CashFilter extends OncePerRequestFilter {

    private static final String SESSION_KEY = "me.jiangcai.dating.inviteBy";
    private static final Pattern inviteFlagPattern = Pattern.compile(".+_inviteBy=(\\d+).*");

    /**
     * @param id 用户的id{@link me.jiangcai.dating.entity.User#id}
     * @return 从id 生成url（一部分）
     */
    public static String guideUserFromId(long id) {
        return "_inviteBy=" + id;
    }

    /**
     * 从地址中解析出邀请者的id
     *
     * @param url url
     * @return 用户id {@link me.jiangcai.dating.entity.User#id}
     */
    public static Long guideUserFromURL(String url) {
        Matcher matcher = inviteFlagPattern.matcher(url);
        if (matcher.matches()) {
            return NumberUtils.parseNumber(matcher.group(1), Long.class);
        }
        return null;
    }

    /**
     * @param request
     * @return 获取当前的邀请者
     */
    public static Long inviteBy(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null)
            return null;
        return (Long) session.getAttribute(SESSION_KEY);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 只要是get 请求 并且url符合规则 就给予session标记
        if (request.getMethod().equalsIgnoreCase("get")) {
            Long id = guideUserFromURL(request.getRequestURL().toString());
            if (id != null) {
                request.getSession(true).setAttribute(SESSION_KEY, id);
            }
        }
        filterChain.doFilter(request, response);
    }

}
