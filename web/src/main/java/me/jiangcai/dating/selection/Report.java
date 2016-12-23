package me.jiangcai.dating.selection;

import lombok.Data;

import java.util.List;

/**
 * @author CJ
 */
@Data
public class Report<T> {
    private final String name;
    private final List<T> data;
    private final List<Selection<T, ?>> selections;

    public Report(String name, List<T> data, List<Selection<T, ?>> selections) {
        this.name = name;
        this.data = data;
        this.selections = selections;
    }
}
