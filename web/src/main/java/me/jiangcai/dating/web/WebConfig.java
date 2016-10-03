package me.jiangcai.dating.web;

import me.jiangcai.dating.core.CoreConfig;
import me.jiangcai.dating.core.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * @author CJ
 */
@Configuration
@Import({CoreConfig.class, MVCConfig.class, WebConfig.Security.class})
public class WebConfig {

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
                    ).antMatchers("/weixin/");
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
                    .logout().logoutUrl("/logout").permitAll();
        }
    }
}