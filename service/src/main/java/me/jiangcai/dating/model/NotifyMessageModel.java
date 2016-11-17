package me.jiangcai.dating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.notify.NotifyType;

/**
 * @author CJ
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyMessageModel {

    private NotifyType type;
    private NotifyMessage message;

}
