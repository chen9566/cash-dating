package me.jiangcai.dating;

import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import javax.persistence.criteria.CriteriaBuilder;
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
     * @return 字段名, 必须跟原Entity名保持一致?不再需要了
     */
    String name();

    boolean searchSupport();

    Predicate searchPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, String word);

    Order order(CriteriaBuilder criteriaBuilder, Sort.Direction direction, Root<?> root);

    Object export(Object origin, MediaType type);

    Selection<?> select(Root<?> root);
}
