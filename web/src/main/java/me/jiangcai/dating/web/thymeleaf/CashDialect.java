package me.jiangcai.dating.web.thymeleaf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CJ
 */
@Component
public class CashDialect extends AbstractDialect implements IDialect, IExpressionObjectDialect, IExpressionObjectFactory {

    private final CashStrings cashStrings;
    @Autowired
    private Share share;

    @Autowired
    public CashDialect(CashStrings cashStrings) {
        super("cash-fating");
        this.cashStrings = cashStrings;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this;
    }

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return new HashSet<>(Arrays.asList("cashStrings", "share"));
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (expressionObjectName.equals("cashStrings"))
            return cashStrings;
        if (expressionObjectName.equals("share"))
            return share;
        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return true;
    }
}
