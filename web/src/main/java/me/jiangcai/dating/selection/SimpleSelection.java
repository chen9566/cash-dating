package me.jiangcai.dating.selection;

import java.util.function.Function;

/**
 * @author CJ
 */
public class SimpleSelection<T, S> implements Selection<T, S> {

    private final String title;
    private final Class<? extends S> type;
    private final Function<T, S> worker;

    public SimpleSelection(String title, Class<? extends S> type, Function<T, S> worker) {
        this.title = title;
        this.type = type;
        this.worker = worker;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Class<? extends S> getTargetType() {
        return type;
    }

    @Override
    public S export(T data) {
        return worker.apply(data);
    }
}
