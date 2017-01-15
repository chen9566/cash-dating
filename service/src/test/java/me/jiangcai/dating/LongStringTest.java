package me.jiangcai.dating;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LongStringTest.class)
public class LongStringTest {

    Random random = new Random();

    @Test
    @Repeat(100)
    public void go() {
        final long l = random.nextLong();
        String str = LongString.toString(l);
        System.out.println(str);
        assertThat(str)
                .hasSize(32);
        assertThat(LongString.toLong(str))
                .isEqualTo(l);
    }

}