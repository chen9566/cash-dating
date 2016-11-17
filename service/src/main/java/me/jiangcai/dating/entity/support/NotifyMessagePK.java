package me.jiangcai.dating.entity.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.jiangcai.dating.notify.NotifyType;

import java.io.Serializable;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyMessagePK implements Serializable {

    private static final long serialVersionUID = -1984836439313895404L;

    private NotifyType notifyType;
    private int version;
}
