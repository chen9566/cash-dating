package me.jiangcai.dating.service;

import me.jiangcai.dating.DataField;
import me.jiangcai.dating.DataFilter;
import me.jiangcai.dating.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 数据查询服务
 *
 * @author CJ
 */
public interface DataService {


    /**
     * 综合搜索
     *
     * @param user       查询者
     * @param search     关键字
     * @param sort       排序字段
     * @param order      方向
     * @param offset     从该索引开始
     * @param limit      总记录的长度
     * @param target     目标实体
     * @param dataFields 字段
     * @param filter     可选的过滤
     * @return 一个Map, total 表示所有的字段,rows则本次需要返回的数据集
     */
    @Transactional(readOnly = true)
    <T> Map<String, ?> data(User user, String search, String sort, Sort.Direction order
            , int offset, int limit, Class<T> target, List<DataField> dataFields, DataFilter<T> filter);

    class UnsearchableField extends DataService.ClassicsField {
        public UnsearchableField(String name, Function<Root<?>, Expression<?>> select) {
            super(name, select);
        }

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

    abstract class ClassicsField implements DataField {

        protected final String name;
        protected final Function<Root<?>, Expression<?>> select;

        public ClassicsField(String name, Function<Root<?>, Expression<?>> select) {
            this.name = name;
            this.select = select;
        }

        public ClassicsField(String name) {
            this(name, null);
        }

        protected Expression<?> selectExpression(Root<?> root) {
            if (select == null)
                return root.get(name());
            return select.apply(root);
        }

        @Override
        public Selection<?> select(CriteriaBuilder builder, CriteriaQuery<?> query, Root<?> root) {
            return selectExpression(root);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Order order(CriteriaBuilder criteriaBuilder, Sort.Direction direction, Root<?> root) {
            if (direction == Sort.Direction.ASC)
                return criteriaBuilder.asc(selectExpression(root));
            return criteriaBuilder.desc(selectExpression(root));
        }

        @Override
        public String hqlSelection(String rootName) {
            return rootName + "." + name;
        }

        @Override
        public Object export(Object origin, MediaType type) {
            return origin;
        }
    }

    /**
     * @author CJ
     */
    class BooleanField extends UnsearchableField {
        public BooleanField(String name, Function<Root<?>, Expression<?>> select) {
            super(name, select);
        }

        public BooleanField(String name) {
            super(name);
        }
    }

    /**
     * @author CJ
     */
    class EnumField extends UnsearchableField {
        public EnumField(String name, Function<Root<?>, Expression<?>> select) {
            super(name, select);
        }

        protected EnumField(String name) {
            super(name);
        }
    }

    /**
     * @author CJ
     */
    class NumberField extends ClassicsField {

        private final Class<? extends Number> numberType;

        public NumberField(String name, Function<Root<?>, Expression<?>> select, Class<? extends Number> numberType) {
            super(name, select);
            this.numberType = numberType;
        }

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

    /**
     * @author CJ
     */
    @SuppressWarnings("SpringJavaAutowiringInspection")
    class StringField extends ClassicsField {

        public StringField(String name, Function<Root<?>, Expression<?>> select) {
            super(name, select);
        }

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
