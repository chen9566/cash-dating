package me.jiangcai.dating.web;

import me.jiangcai.dating.entity.sale.pk.TicketCodePK;
import me.jiangcai.dating.web.advice.TRJNotifyLocker;
import me.jiangcai.dating.web.converter.LocalDateFormatter;
import me.jiangcai.dating.web.converter.ReportHandler;
import me.jiangcai.dating.web.mvc.ImageResolver;
import me.jiangcai.dating.web.thymeleaf.CashDialect;
import me.jiangcai.lib.resource.thymeleaf.ResourceDialect;
import me.jiangcai.wx.web.thymeleaf.WeixinDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * @author CJ
 */
@Configuration
@Import(MVCConfig.ThymeleafConfig.class)
@ComponentScan({"me.jiangcai.dating.web.converter", "me.jiangcai.dating.web.controller", "me.jiangcai.dating.web.advice"})
@EnableWebMvc
class MVCConfig extends WebMvcConfigurerAdapter {
    private static String[] STATIC_RESOURCE_PATHS = new String[]{
            "dist", "css", "fonts", "holder.js", "images", "js", "_resources", "localisation", "admin/js", "users/js"
            , "user/js", "appfile/css", "appfile/images", "appfile/js", "appfile/list_files", "appfile/login_files"
            , "friends/styles", "friends/images"
            , "sale/images", "sale/js", "sale/styles"
    };

    @Autowired
    private Environment environment;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    private LocalDateFormatter localDateFormatter;
    @Autowired
    private ReportHandler reportHandler;
    @Autowired
    private TRJNotifyLocker trjNotifyLocker;

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(trjNotifyLocker).addPathPatterns("/trj/notify/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        registry.addFormatterForFieldType(Sort.Direction.class, new Formatter<Sort.Direction>() {

            @Override
            public String print(Sort.Direction object, Locale locale) {
                if (object == null)
                    return null;
                return object.name().toLowerCase(Locale.ENGLISH);
            }

            @Override
            public Sort.Direction parse(String text, Locale locale) throws ParseException {
                return Sort.Direction.fromStringOrNull(text);
            }
        });
        registry.addFormatterForFieldType(LocalDate.class, localDateFormatter);
        registry.addConverter(new Converter<String, TicketCodePK>() {
            @Override
            public TicketCodePK convert(String source) {
                if (source == null)
                    return null;
                try {
                    return TicketCodePK.valueOf(source);
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ex);
                }
            }
        });
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
        registry.addViewController("/404.html").setViewName("404.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        for (String path : STATIC_RESOURCE_PATHS) {
            registry.addResourceHandler("/" + path + "/**").addResourceLocations("/" + path + "/");
        }

        registry.addResourceHandler("/agreement*.html").addResourceLocations("/");
        registry.addResourceHandler("/mart/**").addResourceLocations("/mart/");
        registry.addResourceHandler("/carinsurance.html").addResourceLocations("/");
        registry.addResourceHandler("/credit.html").addResourceLocations("/");
//        registry.addResourceHandler("/personalok.html").addResourceLocations("/");
    }

    String[] staticResourceAntPatterns() {
        String[] ignoring;
        int startIndex = 0;
        if (environment.acceptsProfiles("development")) {
            ignoring = new String[MVCConfig.STATIC_RESOURCE_PATHS.length + 2];
            ignoring[startIndex++] = "/**/*.html";
            ignoring[startIndex++] = "/mock/**/*";
        } else {
            ignoring = new String[MVCConfig.STATIC_RESOURCE_PATHS.length];
        }
        for (String path : MVCConfig.STATIC_RESOURCE_PATHS) {
            ignoring[startIndex++] = "/" + path + "/**/*";
        }
        return ignoring;
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        super.addReturnValueHandlers(returnValueHandlers);
        returnValueHandlers.add(0, new ImageResolver());
        returnValueHandlers.add(reportHandler);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        registry.viewResolver(thymeleafViewResolver);
    }

    @Import(ThymeleafConfig.ThymeleafTemplateConfig.class)
    static class ThymeleafConfig {
        @Autowired
        private TemplateEngine engine;

        @Bean
        private ThymeleafViewResolver thymeleafViewResolver() {
            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(engine);
            viewResolver.setCharacterEncoding("UTF-8");
            viewResolver.setContentType("text/html;charset=UTF-8");
            return viewResolver;
        }

        @ComponentScan("me.jiangcai.dating.web.thymeleaf")
        static class ThymeleafTemplateConfig {
            @Autowired
            private WebApplicationContext webApplicationContext;
            @SuppressWarnings("SpringJavaAutowiringInspection")
            @Autowired
            private WeixinDialect weixinDialect;
            @Autowired
            private CashDialect cashDialect;
            @Autowired
            private ResourceDialect resourceDialect;

            @Bean
            public TemplateEngine templateEngine() {
                SpringTemplateEngine engine = new SpringTemplateEngine();
                engine.setEnableSpringELCompiler(true);
                engine.setTemplateResolver(templateResolver());
                engine.addDialect(weixinDialect);
                engine.addDialect(cashDialect);
                engine.addDialect(resourceDialect);
                engine.addDialect(new Java8TimeDialect());
                engine.addDialect(new SpringSecurityDialect());
                return engine;
            }

            private ITemplateResolver templateResolver() {
                SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
                resolver.setApplicationContext(webApplicationContext);
                resolver.setCharacterEncoding("UTF-8");
                resolver.setPrefix("/");
                resolver.setTemplateMode(TemplateMode.HTML);
                return resolver;
            }
        }

    }
}
