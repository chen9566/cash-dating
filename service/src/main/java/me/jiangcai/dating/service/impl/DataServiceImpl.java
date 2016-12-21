package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.service.DataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
@Service
public class DataServiceImpl implements DataService {

    private static final Log log = LogFactory.getLog(DataServiceImpl.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    private <X, T> CriteriaQuery<X> where(User user
            , String search
            , CriteriaBuilder criteriaBuilder
            , CriteriaQuery<X> query
            , Root<T> root
            , List<DataField> dataFields, DataFilter<T> filter) {
        Predicate predicate1;
        if (filter != null) {
            predicate1 = filter.dataFilter(user, criteriaBuilder, root);
        } else
            predicate1 = null;

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


    @Override
    public <T> Map<String, ?> data(User user, String search, String sort, Sort.Direction order, int offset, int limit
            , Class<T> target, List<DataField> dataFields, DataFilter<T> filter) {

        if (dataFields.size() < 2) {
            log.error("DataController can not work with less fields");
            throw new RuntimeException();
        }
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<?> query = criteriaBuilder.createQuery();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);

        Root<T> root = query.from(target);
        Root<T> countRoot = countQuery.from(target);

        // select
        CriteriaQuery<?> updatedQuery = query.multiselect(dataFields.stream()
                .map(dataField
                        -> dataField.select(criteriaBuilder, query, root))
                .collect(Collectors.toList()));

        countQuery = countQuery.select(criteriaBuilder.count(countRoot));

        // where
        updatedQuery = where(user, search, criteriaBuilder, updatedQuery, root, dataFields, filter);
        countQuery = where(user, search, criteriaBuilder, countQuery, countRoot, dataFields, filter);

        // sort order
        Order defaultOrder = filter.defaultOrder(criteriaBuilder, root);
        if (!StringUtils.isEmpty(sort)) {
            final Order order1 = dataFields.stream()
                    .filter(dataField -> dataField.name().equals(sort))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new)
                    .order(criteriaBuilder, order, root);
            if (defaultOrder != null)
                updatedQuery = updatedQuery.orderBy(order1, defaultOrder);
            else
                updatedQuery = updatedQuery.orderBy(order1);
        } else {
            if (defaultOrder != null)
                updatedQuery = updatedQuery.orderBy(defaultOrder);
        }


        // limit
        long total = entityManager.createQuery(countQuery).getSingleResult();
        List<?> list = entityManager.createQuery(updatedQuery)
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
}
