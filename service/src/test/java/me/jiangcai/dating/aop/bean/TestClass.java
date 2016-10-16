package me.jiangcai.dating.aop.bean;

import me.jiangcai.dating.ThreadSafe;
import me.jiangcai.dating.aop.TestLocker;
import me.jiangcai.dating.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CJ
 */
@Service
public class TestClass {

    @Autowired
    private SystemService systemService;
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @ThreadSafe
    public void hello(TestLocker locker) {
        System.out.println(locker);
    }

    @ThreadSafe
    public void hello(TestLocker locker, Object another) {
        System.out.println(locker);
    }

    @ThreadSafe
    public void throwable(TestLocker locker) throws Exception {
        throw new Exception("无他");
    }

    public void real(TestLocker testLocker) {
        String key = "testV";
        if (systemService.getSystemString(key, String.class, null).equals("0")) {
            systemService.updateSystemString(key, String.valueOf(atomicInteger.incrementAndGet()));
        }
    }

    @ThreadSafe
    public void real2(TestLocker testLocker) {
        real(testLocker);
    }
}
