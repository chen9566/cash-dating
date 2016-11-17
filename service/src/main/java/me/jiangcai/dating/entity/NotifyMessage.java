package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.dating.entity.support.NotifyMessagePK;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageStyle;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author CJ
 */
@Setter
@Getter
@IdClass(NotifyMessagePK.class)
@Entity
public class NotifyMessage implements TemplateMessageStyle {

    @Id
    private NotifyType notifyType;
    @Id
    private int version;
    private boolean enabled;
    @Column(length = 30)
    private String templateIdShort;
    @Column(length = 30)
    private String templateTitle;
    @Column(length = 30)
    private String industryId;
    @Column(length = 100)
    private String templateId;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NotifyMessageParameter> messageParameters;

    @Override
    @Transient
    public Collection<TemplateMessageParameter> parameterStyles() {
        return Collections.unmodifiableSet(messageParameters);
    }
}
