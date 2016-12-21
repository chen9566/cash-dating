package me.jiangcai.dating;

import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 * @author CJ
 */
public interface DataField {

    static Predicate stringLike(CriteriaBuilder criteriaBuilder, String word, Path<String> path) {
        if (!word.startsWith("%"))
            word = "%" + word;
        if (!word.endsWith("%"))
            word = word + "%";
        return criteriaBuilder.like(path, word);
    }

    /**
     * @return 字段名, 逻辑名称,自定义
     */
    String name();

    boolean searchSupport();

    Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word);

    Order order(CriteriaBuilder criteriaBuilder, Sort.Direction direction, Root<?> root);

    Object export(Object origin, MediaType type);

    /**
     * 这个字段所选择的JPA
     *
     * @param builder builder
     * @param query   所使用的query
     * @param root    当前From
     * @return 要返回的字段
     */
    Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root);

    /**
     * @param rootName 当前From的别名
     * @return hql选择
     */
    String hqlSelection(String rootName);

    /**
     * 是否需要链接到更多的表
     *
     * @param rootName 当前From的别名
     * @return 如果无需请返回空字符串
     */
    default String hqlFromMore(String rootName) {
        return "";
    }
}
