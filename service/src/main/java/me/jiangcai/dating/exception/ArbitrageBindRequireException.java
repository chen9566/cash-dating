package me.jiangcai.dating.exception;

import lombok.Getter;

/**
 * 需要绑定,用户要确认这个操作
 *
 * @author CJ
 */
@Getter
public class ArbitrageBindRequireException extends Exception {
    private final boolean manageable;

    public ArbitrageBindRequireException(boolean manageable) {
        this.manageable = manageable;
    }
}
