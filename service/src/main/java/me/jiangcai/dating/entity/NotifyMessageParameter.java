package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.wx.model.message.TemplateMessageParameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.awt.*;
import java.text.MessageFormat;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class NotifyMessageParameter implements TemplateMessageParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Color defaultColor;
    private String pattern;

    @Override
    @Transient
    public MessageFormat getFormat() {
        return new MessageFormat(pattern);
    }


}
