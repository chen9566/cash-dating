package me.jiangcai.dating.aop;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.aop.bean.TestClass;
import me.jiangcai.dating.repository.SystemStringRepository;
import me.jiangcai.dating.service.SystemService;
import org.apache.xpath.operations.String;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@ContextConfiguration(classes = JoinTest.Config.class)
public class JoinTest extends ServiceBaseTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TestClass testClass;
    @Autowired
    private SystemService systemService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SystemStringRepository systemStringRepository;

    @Test
    public void go1() throws InterruptedException {
        testClass.hello(new TestLocker());
        testClass.hello(new TestLocker(), null);

        // 一个实际情况 类似订单 我们会设置一个订单 成功了! 同时多个线程调用它 检查是否只有一个输出!
        systemService.updateSystemString("testV", "0");
//        systemStringRepository.delete("testV");

        TestLocker testLocker = new TestLocker();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                testClass.real2(testLocker);
            }
        };

        // 20个线程一起干
        int count = 20;
        while (count-- > 0) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
        }

        Thread.sleep(2000L);

        assertThat(systemService.getSystemString("testV", String.class, null))
                .isEqualTo("1");
    }

    @Test(expected = Exception.class)
    public void go2() throws Exception {
        testClass.throwable(new TestLocker());
    }

    @ComponentScan("me.jiangcai.dating.aop.bean")
    static class Config {
    }


}