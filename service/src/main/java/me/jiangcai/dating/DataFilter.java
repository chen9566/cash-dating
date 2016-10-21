package me.jiangcai.dating;

import me.jiangcai.dating.entity.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author CJ
 */
public interface DataFilter<T> {
    Predicate dataFilter(User user, CriteriaBuilder criteriaBuilder, Root<T> root);
}
