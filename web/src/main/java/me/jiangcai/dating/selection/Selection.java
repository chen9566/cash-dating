package me.jiangcai.dating.selection;

/**
 * @author CJ
 */
public interface Selection<T, S> {
    String getTitle();

    Class<? extends S> getTargetType();

    S export(T data);
}
