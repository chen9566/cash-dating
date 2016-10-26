package me.jiangcai.dating.web;

import me.jiangcai.dating.core.CoreConfig;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author CJ
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Import({CoreConfig.class, MVCConfig.class, WebConfig.Security.class})
public class WebConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public void registerSharedAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(username -> {
            // 在测试区域 支持登录管理员
//            if (BuildIn_ROOT.equals(username) && environment.acceptsProfiles("test")) {
//                Manager manager = new Manager();
//                manager.setPassword(passwordEncoder.encode(BuildIn_Password));
//                return manager;
//            }
            UserDetails userDetails = userRepository.findByOpenId(username);
            if (userDetails == null)
                throw new UsernameNotFoundException("没有找到用户名");
            return userDetails;
        });
        auth.authenticationProvider(provider);
    }

    @EnableWebSecurity
    @Order(99)//毕竟不是老大 100就让给别人了
    public static class Security extends WebSecurityConfigurerAdapter {

        @Autowired
        private Environment environment;
        @Autowired
        private MVCConfig mvcConfig;

        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);

            web.ignoring()
                    .antMatchers(
                            // 安全系统无关的uri
                            mvcConfig.staticResourceAntPatterns()
                    )
                    .antMatchers("/404.html")
                    .antMatchers("/weixin/")
                    .antMatchers("/notify/**")
                    .antMatchers("/login");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            // 在测试环境下 随意上传
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry =
                    http.antMatcher("/**")
                            .authorizeRequests();
//            if (environment.acceptsProfiles("test") || environment.acceptsProfiles("development")) {//测试阶段或者开发阶段
//                registry = registry
////                    .anyRequest().permitAll()//不妨这样  不要这样! 安全也是业务的一部分 同样需要测试,此处许可仅仅是为了原型测试。
//                        .antMatchers("/manage/upload").permitAll()
//                        .antMatchers("/manage/upload/fine").permitAll()
//                        .antMatchers("/manage/widget/widgets").permitAll()
//                        .antMatchers("/manage/owners").permitAll();
//            }

            registry
                    .antMatchers("/provinceList").permitAll()
                    .antMatchers("/subBranchList").permitAll()
                    // 所有的
                    .antMatchers("/all.js").permitAll()
                    // 非微信登录要获取的二维码
                    .antMatchers("/loginToken/**").permitAll()
                    // 支付分享的
                    .antMatchers("/inviteQR/**").permitAll()
                    .antMatchers("/order/**").permitAll()
                    .antMatchers("/orderCompleted/**").permitAll()
                    // 给他人支付
                    .antMatchers("/to/**").permitAll()
                    .antMatchers("/to").permitAll()
//                    .antMatchers("/toPay/**").permitAll()
//                    .antMatchers("/toPayQR/**").permitAll()
                    // 有几个事情是没有登录也可以做的 比如说 忘记密码 注册
                    .antMatchers("/verificationCode").permitAll()// put 发送验证码
                    .antMatchers("/registerMobile").permitAll()// post注册手机
                    .antMatchers("/registerCard").permitAll()// post注册第一张银行卡
                    .antMatchers("/forgetPassword").permitAll()//get,post 忘记密码
                    // 有几个事情是没有登录也可以做的 比如说 忘记密码 注册
                    .antMatchers("/**").authenticated()
                    .antMatchers("/manage/**").hasAnyRole(Login.Role_Manage_Value, "ROOT")
                    .antMatchers("/manage/root/**").hasRole("ROOT")
// 更多权限控制
                    .and().csrf().disable()
                    .formLogin()
//                .failureHandler()
//                    .loginProcessingUrl("/manage/auth")
                    .loginPage("/login")
                    .failureUrl("/login?type=error")
                    .permitAll()
                    .and()
                    .logout().logoutUrl("/logout").permitAll()
                    .logoutSuccessUrl("/justLogout").permitAll();
        }
    }
}
