package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.service.impl.DataServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;

/**
 * 一个临时解决方案
 *
 * @author CJ
 */
@Controller
@RequestMapping(value = "/manage/data/user")
public class UserController2 extends UserController {

    private static final Log log = LogFactory.getLog(UserController2.class);
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @Transactional(readOnly = true)
    public Object data(@AuthenticationPrincipal User user, String search, String sort, Sort.Direction order
            , int offset, int limit) {
        StringBuilder countSQL = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        List<DataField> fields = fieldList();

        // 对象只有一个 它的别名是u
        countSQL.append("select count(u) from User as u ");
        sql.append("select ");
        for (int i = 0; i < fields.size(); i++) {
            DataField dataField = fields.get(i);
            if (i > 0)
                sql.append(",");
            sql.append(dataField.hqlSelection("u")).append(" as ").append(dataField.name()).append(" ");
        }

        sql.append("from User as u ");
        // left join xx
        for (DataField dataField : fields) {
            sql.append(dataField.hqlFromMore("u")).append(" ");
            countSQL.append(dataField.hqlFromMore("u")).append(" ");
        }

        sql.append("where ");
        countSQL.append("where ");
        //此处有共同条件
        commonPredicate(sql);
        commonPredicate(countSQL);

        if (!StringUtils.isEmpty(search)) {
            searchPredicate(search, sql, fields);
            searchPredicate(search, countSQL, fields);
        }

        if (!StringUtils.isEmpty(sort) && order != null) {
            fields.stream().filter(dataField -> dataField.name().equals(sort))
                    .findFirst()
                    .ifPresent(dataField -> {
                        sql.append("order by ");
                        sql.append(dataField.hqlSelection("u")).append(" ");
                        sql.append(order.name());
                    });

        }

        log.debug(sql.toString());
        log.debug(countSQL.toString());

        final Query dataQuery = entityManager.createQuery(sql.toString());
        final TypedQuery<Long> countQuery = entityManager.createQuery(countSQL.toString(), Long.class);


        commonPredicate(countQuery);
        commonPredicate(dataQuery);
        if (!StringUtils.isEmpty(search)) {
            // 字符串? 或者 指定的数据格式?
//            countQuery.setp
            searchPredicate(search, countQuery, fields);
            searchPredicate(search, dataQuery, fields);
//            int pIndex = 0;
//            for (DataField dataField : fields) {
//                if (dataField.searchSupport()) {
//                    pIndex++;
//                    countQuery.setParameter(pIndex, "%" + search + "%");
//                    dataQuery.setParameter(pIndex, "%" + search + "%");
//                }
//            }
        }


        long total = countQuery.getSingleResult();

        List<?> list = dataQuery
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        // to json
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", total);
        DataServiceImpl.printListIntoMap(fields, list, result);
        return result;
    }

    private void searchPredicate(String search, Query query, List<DataField> fields) {
        fields.stream()
                .filter(DataField::searchSupport)
                .map(DataField::getTargetType)
                .distinct()
                .forEach(type -> {
                    if (type == String.class)
                        query.setParameter("searchText", "%" + search + "%");
                    else if (Number.class.isAssignableFrom(type)) {
                        try {
                            //noinspection unchecked
                            query.setParameter("search" + type.getSimpleName(), NumberUtils.parseNumber(search, (Class<Number>) type));
                        } catch (Throwable ignored) {
                        }
                    }
                });


    }

    @SuppressWarnings("unchecked")
    private void searchPredicate(String search, StringBuilder sql, List<DataField> fields) {
        sql.append("AND (");
        int x = 0;
        for (DataField dataField : fields) {
            if (dataField.searchSupport()) {
                if (dataField.getTargetType() == String.class) {
                    if (x > 0) {
                        sql.append("or ");
                    }
                    sql.append(dataField.hqlSelection("u")).append(" like :searchText ");
                    x++;
                } else if (Number.class.isAssignableFrom(dataField.getTargetType())) {
                    if (StringNumber(search, (Class<Number>) dataField.getTargetType())) {
                        if (x > 0) {
                            sql.append("or ");
                        }
                        sql.append(dataField.hqlSelection("u")).append(" = :search").append(dataField.getTargetType().getSimpleName()).append(" ");
                        x++;
                    }
                } else {
                    throw new IllegalArgumentException("can not search those dataField:" + dataField.getTargetType());
                }

            }
        }
        sql.append(") ");
    }

    private boolean StringNumber(String text, Class<Number> numberClass) {
        try {
            NumberUtils.parseNumber(text, numberClass);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void commonPredicate(Query query) {
        query.setParameter("root", ManageStatus.root);
    }

    private void commonPredicate(StringBuilder sql) {
        sql.append("(u.manageStatus IS NULL or u.manageStatus <> :root) and u.mobileNumber IS NOT NULL ");
    }
}
