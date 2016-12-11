package me.jiangcai.dating.service;

import lombok.SneakyThrows;
import me.jiangcai.lib.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.function.Function;

/**
 * @author CJ
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataResourceField extends DataService.StringField {

    @Autowired
    private ResourceService resourceService;

    public DataResourceField(String name, Function<Root<?>, Expression<?>> select) {
        super(name, select);
    }

    public DataResourceField(String name) {
        super(name);
    }

    @Override
    @SneakyThrows(IOException.class)
    public Object export(Object origin, MediaType type) {
        if (origin == null)
            return null;
        return resourceService.getResource(origin.toString()).httpUrl().toString();
    }
}
