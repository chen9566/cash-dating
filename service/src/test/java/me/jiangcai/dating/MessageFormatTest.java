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
//
//    @Test
//    public void images() {
//        String all = "12.11.1.JPG  13.1.5.JPG    14.5.3.JPG   15.6.16.JPG  15.8.30.JPG   IMG_1529.JPG  IMG_2206.JPG\n" +
//                "        12.11.JPG    13.12.22.JPG  14.5.6.JPG   15.6.2.JPG   16.3.16.JPG   IMG_1534.JPG  IMG_2242.JPG\n" +
//                "        12.12.JPG    13.12.29.JPG  14.6.12.JPG  15.6.20.JPG  16.3.2.JPG    IMG_1541.JPG  IMG_2552.JPG\n" +
//                "        12.3.21.JPG  13.12.5.JPG   14.6.21.JPG  15.7.13.JPG  16.3.20.JPG   IMG_1625.JPG  IMG_2606.JPG\n" +
//                "        12.4.20.JPG  13.7.20.JPG   14.6.26.JPG  15.7.22.JPG  16.6.21.JPG   IMG_1640.JPG  IMG_3041.JPG\n" +
//                "        12.6.25.JPG  14.1.3.JPG    14.7.10.JPG  15.7.28.JPG  16.7.8.JPG    IMG_1737.JPG  IMG_3677.JPG\n" +
//                "        12.8.8.JPG   14.2.14.JPG   14.8.24.JPG  15.8.16.JPG  16.9.8.JPG    IMG_1765.JPG\n" +
//                "        12.9.17.JPG  14.2.5.JPG    15.5.26.JPG  15.8.19.JPG  IMG_1413.JPG  IMG_1875.JPG\n" +
//                "        12.9.18.JPG  14.4.29.JPG   15.5.31.JPG  15.8.23.JPG  IMG_1425.JPG  IMG_1913.JPG";
//
//        Scanner scanner = new Scanner(all);
//        Pattern pattern = Pattern.compile("\\s+");
////        while (scanner.hasNext(pattern)){
////            System.out.println(scanner.next(pattern));
////        }
//        scanner = scanner.useDelimiter(pattern);
//        while (scanner.hasNext()) {
//            final String next = scanner.next();
////            System.out.println(next);
//            System.out.print("<img src=\"");
//            System.out.print(next);
//            System.out.print("\"");
//            System.out.print("/>");
//        }
//    }

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
