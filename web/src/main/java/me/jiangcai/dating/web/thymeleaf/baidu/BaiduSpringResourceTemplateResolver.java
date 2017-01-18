package me.jiangcai.dating.web.thymeleaf.baidu;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author CJ
 */
public class BaiduSpringResourceTemplateResolver extends SpringResourceTemplateResolver {

    private final String code;

    public BaiduSpringResourceTemplateResolver(String code) throws IOException {
        try (InputStream propertiesFile = new ClassPathResource("/velocity.properties").getInputStream()) {
            Properties properties = new Properties();
            properties.load(propertiesFile);
            Velocity.init(properties);
        }
        Template javascript = Velocity.getTemplate("/baidu.html.vm");
        VelocityContext context = new VelocityContext();
        context.put("code", code);

        StringWriter writer = new StringWriter();
        javascript.merge(context, writer);
        this.code = writer.toString();
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate
            , String template, String resourceName, String characterEncoding
            , Map<String, Object> templateResolutionAttributes) {
        return new BaiduTemplateResource(code, super.computeTemplateResource(configuration, ownerTemplate, template
                , resourceName, characterEncoding, templateResolutionAttributes));
    }
}
