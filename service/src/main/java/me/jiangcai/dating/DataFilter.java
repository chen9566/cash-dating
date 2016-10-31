package me.jiangcai.dating;

import me.jiangcai.dating.entity.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author CJ
 */
public interface DataFilter<T> {
    Predicate dataFilter(User user, CriteriaBuilder criteriaBuilder, Root<T> root);

    /**
     * 默认排序
     *
     * @param criteriaBuilder cb
     * @param root            root
     * @return null if has no default order
     */
    default Order defaultOrder(CriteriaBuilder criteriaBuilder, Root<T> root) {
        return null;
    }
}
