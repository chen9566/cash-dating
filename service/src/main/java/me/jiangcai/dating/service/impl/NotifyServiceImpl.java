package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.Time8Utils;
import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.NotifyMessageParameter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.NotifyMessagePK;
import me.jiangcai.dating.model.NotifyMessageModel;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.repository.NotifyMessageRepository;
import me.jiangcai.dating.service.NotifyService;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageStyle;
import me.jiangcai.wx.protocol.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class NotifyServiceImpl implements NotifyService {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private NotifyMessageRepository notifyMessageRepository;
    @Autowired
    private PublicAccountSupplier supplier;

    @Override
    public List<NotifyMessageModel> allTemplate() {
        List<NotifyMessage> messages = notifyMessageRepository.findAll();

        ArrayList<NotifyMessageModel> messageModels = new ArrayList<>();
        Stream.of(NotifyType.values())
                .forEach(notifyType -> {
                    NotifyMessageModel messageModel = new NotifyMessageModel();
                    messageModel.setType(notifyType);
                    messageModel.setMessage(messages.stream()
                            .filter(notifyMessage
                                    -> notifyMessage.getNotifyType() == notifyType
                                    && notifyMessage.getVersion() == notifyType.getLastVersion())
                            .findFirst()
                            .orElse(null));
                    messageModels.add(messageModel);
                });
        return messageModels;
    }

    @Override
    public NotifyMessage forType(NotifyType type) {
        return notifyMessageRepository.findOne(new NotifyMessagePK(type, type.getLastVersion()));
    }

    @Override
    public NotifyMessage save(NotifyMessage message) {
        return notifyMessageRepository.save(message);
    }

    @Override
    public void sendMessage(User user, String url, NotifyMessage message, Set<NotifyMessageParameter> parameters
            , Object... vars) {
        // 把LocalDateTime 转成Date
        for (int i = 0; i < vars.length; i++) {
//            LocalDateTime localDateTime;
            if (vars[i] != null) {
                if (vars[i] instanceof LocalDateTime) {
                    LocalDateTime src = (LocalDateTime) vars[i];
                    vars[i] = Time8Utils.toDate(src);
                }
            }
        }
        Protocol protocol = Protocol.forAccount(supplier.findByIdentifier(null));
        protocol.sendTemplate(user.getOpenId(), new TemplateMessageStyle() {
            @Override
            public Collection<? extends TemplateMessageParameter> parameterStyles() {
                return parameters;
            }

            @Override
            public String getTemplateIdShort() {
                return null;
            }

            @Override
            public String getTemplateTitle() {
                return null;
            }

            @Override
            public String getIndustryId() {
                return null;
            }

            @Override
            public String getTemplateId() {
                return message.getTemplateId();
            }

            @Override
            public void setTemplateId(String templateId) {
                message.setTemplateId(templateId);
            }
        }, url, null, vars);
    }
}
