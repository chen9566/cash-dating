package me.jiangcai.dating.web.thymeleaf.baidu;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
public class BaiduTemplateResource implements ITemplateResource {

    private final String code;
    private final ITemplateResource resource;

    BaiduTemplateResource(String code, ITemplateResource resource) {
        this.resource = resource;
        this.code = code;
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }

    @Override
    public String getBaseName() {
        return resource.getBaseName();
    }

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public Reader reader() throws IOException {
        String html = IOUtils.readLines(resource.reader()).stream()
                .collect(Collectors.joining());
        Document document = Jsoup.parse(html);
        document.getElementsByTag("head").stream()
                .findFirst().ifPresent(head -> head.append(code));
        return new StringReader(document.outerHtml());
    }

    @Override
    public ITemplateResource relative(String relativeLocation) {
        return resource.relative(relativeLocation);
    }

}
