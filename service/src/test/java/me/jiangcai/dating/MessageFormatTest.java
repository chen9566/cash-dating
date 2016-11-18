package me.jiangcai.dating;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class MessageFormatTest {

    @Test
    public void test() {
        MessageFormat messageFormat = new MessageFormat("{0}");

        assertThat(messageFormat.format(new Object[]{"abc"}))
                .isEqualTo("abc");
        // 时间
        messageFormat = new MessageFormat("{0},{1,date,yyyy-MM-dd HH:mm:ss}");
        System.out.println(messageFormat.format(new Object[]{"abc", Time8Utils.toDate(LocalDateTime.now())}));
        messageFormat = new MessageFormat("{0},{1,date,full}");
        System.out.println(messageFormat.format(new Object[]{"abc", Time8Utils.toDate(LocalDateTime.now())}));
        messageFormat = new MessageFormat("{0},{1,date,long}");
        System.out.println(messageFormat.format(new Object[]{"abc", Time8Utils.toDate(LocalDateTime.now())}));
        messageFormat = new MessageFormat("{0},{1,date,medium}");
        System.out.println(messageFormat.format(new Object[]{"abc", Time8Utils.toDate(LocalDateTime.now())}));
        messageFormat = new MessageFormat("{0},{1,date,short}");
        System.out.println(messageFormat.format(new Object[]{"abc", Time8Utils.toDate(LocalDateTime.now())}));
        // Number

        final Object number = newNumber();
        messageFormat = new MessageFormat("{0},{1,number,￥,###.##}");
        System.out.println(messageFormat.format(new Object[]{"abc", number}));
        messageFormat = new MessageFormat("{0},{1,number,￥,##}");
        System.out.println(messageFormat.format(new Object[]{"abc", number}));
        messageFormat = new MessageFormat("{0},{1,number,integer}");
        System.out.println(messageFormat.format(new Object[]{"abc", number}));
        messageFormat = new MessageFormat("{0},{1,number,currency}");
        System.out.println(messageFormat.format(new Object[]{"abc", number}));
        messageFormat = new MessageFormat("{0},{1,number,percent}");
        System.out.println(messageFormat.format(new Object[]{"abc", number}));


    }

    private Object newNumber() {
        return new BigDecimal(new Random().nextDouble()).add(new BigDecimal(new Random().nextInt()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
