package me.jiangcai.dating.web.controller.support;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.DataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 只有管理员可以获取数据
 * http://localhost:63342/cash-dating/web/src/main/webapp/mock/users.json?search=ok&sort=city&order=asc&offset=0&limit=10
 *
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Manage_Value + "')")
public abstract class DataController<T> {

    private static final Log log = LogFactory.getLog(DataController.class);

    @Autowired
    private DataService dataService;
    @Autowired
    private ConversionService conversionService;

//    @Autowired
//    private JpaSpecificationExecutor<T> jpaSpecificationExecutor;

    protected abstract Class<T> type();

    protected abstract List<DataField> fieldList();

    protected abstract DataFilter<T> dataFilter();

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object data(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        return dataService.data(user, search, sort, order, offset, limit, type(), fieldList(), dataFilter());
    }

    protected class ToStringField extends DataService.UnsearchableField {

        public ToStringField(String name) {
            super(name);
        }

        @Override
        public Object export(Object origin, MediaType type) {
            if (origin == null)
                return null;
            return conversionService.convert(origin, String.class);
//            return super.export(origin, type);
        }
    }


}
