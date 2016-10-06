package me.jiangcai.dating.web.mvc;

import org.springframework.util.NumberUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author CJ
 */
public class CashFilter extends OncePerRequestFilter {

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

}
