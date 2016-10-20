package me.jiangcai.dating.web.controller.support;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.persistence.internal.jpa.querydef.OrderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    private EntityManager entityManager;
    @Autowired
    private ConversionService conversionService;

//    @Autowired
//    private JpaSpecificationExecutor<T> jpaSpecificationExecutor;

    protected abstract Class<T> type();

    protected abstract List<DataField> fieldList();

    protected Predicate dataFilter(User user, CriteriaBuilder criteriaBuilder, Root<T> root) {
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object data(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        final List<DataField> dataFields = fieldList();
        if (dataFields.size() < 2) {
            log.error("DataController can not work with less fields");
            throw new RuntimeException();
        }
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query = criteriaBuilder.createQuery();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root<T> root = query.from(type());
        Root<T> countRoot = countQuery.from(type());

        // select
        query = query.multiselect(dataFields.stream()
                .map(dataField
                        -> dataField.select(root))
                .collect(Collectors.toList()));

        countQuery = countQuery.select(criteriaBuilder.count(countRoot));

        // where
        query = where(user, search, criteriaBuilder, query, root, dataFields);
        countQuery = where(user, search, criteriaBuilder, countQuery, countRoot, dataFields);

        // sort order
        if (!StringUtils.isEmpty(sort))
            query = query.orderBy(dataFields.stream()
                    .filter(dataField -> dataField.name().equals(sort))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new)
                    .order(order, root));

        // limit
        long total = entityManager.createQuery(countQuery).getSingleResult();
        List<?> list = entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        // to json
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", list.stream()
                .map(o -> {
                    Object[] data = (Object[]) o;
                    HashMap<String, Object> row = new HashMap<>();
                    if (data == null)
                        return null;
                    for (int i = 0; i < data.length; i++) {
                        DataField name = dataFields.get(i);
                        row.put(name.name(), name.export(data[i], MediaType.APPLICATION_JSON));
                    }
                    return row;
                }).collect(Collectors.toList()));

        return result;
    }

    private <X> CriteriaQuery<X> where(User user
            , String search
            , CriteriaBuilder criteriaBuilder
            , CriteriaQuery<X> query
            , Root<T> root
            , List<DataField> dataFields) {
        Predicate predicate1 = dataFilter(user, criteriaBuilder, root);

        if (StringUtils.isEmpty(search)) {
            if (predicate1 != null)
                query = query.where(predicate1);
        } else {
            List<Predicate> conditions = dataFields.stream()
                    .filter(DataField::searchSupport)
                    .map(dataField -> dataField.searchPredicate(criteriaBuilder, root, search))
                    .filter(predicate -> predicate != null)
                    .collect(Collectors.toList());

            Predicate condition = criteriaBuilder.or(conditions.toArray(new Predicate[conditions.size()]));
            if (predicate1 != null)
                query = query.where(predicate1, condition);
            else
                query = query.where(condition);
        }
        return query;
    }

    protected interface DataField {

        static Predicate stringLike(CriteriaBuilder criteriaBuilder, String word, Path<String> path) {
            if (!word.startsWith("%"))
                word = "%" + word;
            if (!word.endsWith("%"))
                word = word + "%";
            return criteriaBuilder.like(path, word);
        }

        /**
         * @return 字段名, 必须跟原Entity名保持一致?不再需要了
         */
        String name();

        boolean searchSupport();

        Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word);

        Order order(Sort.Direction direction, Root<?> root);

        Object export(Object origin, MediaType type);

        Selection<?> select(Root<?> root);
    }

    protected abstract class ClassicsField implements DataField {

        protected final String name;

        public ClassicsField(String name) {
            this.name = name;
        }

        protected Expression<?> selectExpression(Root<?> root) {
            return root.get(name());
        }

        @Override
        public Selection<?> select(Root<?> root) {
            return selectExpression(root);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Order order(Sort.Direction direction, Root<?> root) {
            return new OrderImpl(selectExpression(root), direction == Sort.Direction.ASC);
        }

        @Override
        public Object export(Object origin, MediaType type) {
            return origin;
        }
    }

    protected class UnsearchableField extends ClassicsField {

        public UnsearchableField(String name) {
            super(name);
        }

        @Override
        public boolean searchSupport() {
            return false;
        }

        @Override
        public Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word) {
            return null;
        }
    }

    protected class NumberField extends ClassicsField {

        private final Class<? extends Number> numberType;

        public NumberField(String name, Class<? extends Number> numberType) {
            super(name);
            this.numberType = numberType;
        }

        @Override
        public boolean searchSupport() {
            return true;
        }

        @Override
        public Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word) {
            try {
                return criteriaBuilder.equal(selectExpression(root), NumberUtils.parseNumber(word, numberType));
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    protected class ToStringField extends UnsearchableField {

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

    protected class EnumField extends UnsearchableField {

        public EnumField(String name) {
            super(name);
        }
    }

    protected class BooleanField extends UnsearchableField {

        public BooleanField(String name) {
            super(name);
        }
    }

    protected class StringField extends ClassicsField {

        public StringField(String name) {
            super(name);
        }

        @Override
        public boolean searchSupport() {
            return true;
        }

        @Override
        public Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word) {
            @SuppressWarnings("unchecked") final Path<String> path = (Path<String>) selectExpression(root);
            return DataField.stringLike(criteriaBuilder, word, path);
        }

    }


}
