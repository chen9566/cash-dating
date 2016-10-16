package me.jiangcai.dating.aop;

import me.jiangcai.dating.Locker;

/**
 * @author CJ
 */
public class TestLocker implements Locker {
    @Override
    public Object lockObject() {
        return this;
    }
}
