package me.jiangcai.dating.model;

import java.util.function.Function;

/**
 * @author CJ
 */
public enum VerificationType {
    register;

    public Function<String, String> work() {
        return Function.identity();
    }
}
