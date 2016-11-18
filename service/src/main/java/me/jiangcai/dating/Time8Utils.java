package me.jiangcai.dating;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
public class Time8Utils {

    public static Date toDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

}
