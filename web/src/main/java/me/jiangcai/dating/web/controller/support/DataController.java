package me.jiangcai.dating.web.controller.support;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.User;
import org.eclipse.persistence.internal.jpa.querydef.OrderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    @Autowired
    private EntityManager entityManager;

    protected abstract Class<T> type();

//    @Autowired
//    private JpaSpecificationExecutor<T> jpaSpecificationExecutor;

    protected abstract List<DataField> fieldList();

    protected Predicate dataFilter(User user) {
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object data(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query = criteriaBuilder.createQuery();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root<T> root = query.from(type());
        Root<T> countRoot = countQuery.from(type());

        // select
        final List<DataField> dataFields = fieldList();
        query = query.multiselect(dataFields.stream()
                .map(dataField
                        -> root.get(dataField.name()))
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


        System.out.println(total);
        System.out.println(list);

        return null;
    }

    private <X> CriteriaQuery<X> where(User user
            , String search
            , CriteriaBuilder criteriaBuilder
            , CriteriaQuery<X> query
            , Root<T> root
            , List<DataField> dataFields) {
        Predicate predicate1 = dataFilter(user);

        if (StringUtils.isEmpty(search)) {
            if (predicate1 != null)
                query = query.where(predicate1);
        } else {
            List<Predicate> conditions = dataFields.stream()
                    .filter(DataField::searchSupport)
                    .map(dataField -> dataField.searchPredicate(criteriaBuilder, root, search))
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

        String name();

        boolean searchSupport();

        Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word);

        Order order(Sort.Direction direction, Root<?> root);
    }

    protected class StringField implements DataField {
        private final String name;

        public StringField(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public boolean searchSupport() {
            return true;
        }

        @Override
        public Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word) {
            if (!word.startsWith("%"))
                word = "%" + word;
            if (!word.endsWith("%"))
                word = word + "%";

            return criteriaBuilder.like(root.get(name()), word);
        }

        @Override
        public Order order(Sort.Direction direction, Root<?> root) {
            return new OrderImpl(root.get(name()), direction == Sort.Direction.ASC);
        }
    }


}
