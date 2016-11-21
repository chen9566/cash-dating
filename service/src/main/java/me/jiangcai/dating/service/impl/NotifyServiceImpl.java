package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.NotifyMessageParameter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.support.NotifyMessagePK;
import me.jiangcai.dating.event.Notification;
import me.jiangcai.dating.model.NotifyMessageModel;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.repository.NotifyMessageRepository;
import me.jiangcai.dating.service.NotifyService;
import me.jiangcai.lib.seext.TimeUtils;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.message.TemplateMessageParameter;
import me.jiangcai.wx.model.message.TemplateMessageStyle;
import me.jiangcai.wx.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    private static final Log log = LogFactory.getLog(NotifyServiceImpl.class);
    private final Map<Notification, LocalDateTime> times = Collections.synchronizedMap(new HashMap<>());
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private NotifyMessageRepository notifyMessageRepository;
    @Autowired
    private PublicAccountSupplier supplier;
    private ExecutorService executorService = Executors.newWorkStealingPool();
    @Autowired
    private Environment environment;

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
    public void sendMessage(Notification notification) {
        NotifyMessage message = forType(notification.getType());
        if (message == null)
            return;
        if (!message.isEnabled())
            return;

        // 移除2分钟前的
        times.keySet().stream()
                .filter(notification1 -> times.get(notification1).isBefore(LocalDateTime.now().minusMinutes(-2)))
                .forEach(times::remove);
        if (times.containsKey(notification))
            return;
        times.put(notification, LocalDateTime.now());
        String url;
        if (notification.getUri() == null) {
            url = null;
        } else {
            url = environment.getProperty("dating.url", "http://localhost") + notification.getUri();
        }
        executorService.submit(() -> {
            try {
                sendMessage(notification.getUser(), url, message, message.getMessageParameters(), notification.getVars());
            } catch (Throwable ex) {
                log.warn("Notify", ex);
            }
        });
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
                    vars[i] = TimeUtils.toDate(src, null);
                }
            }
        }
        Protocol protocol = Protocol.forAccount(supplier.findByIdentifier(null));
        protocol.sendTemplate(
                user.getOpenId()
//                "oiKvNt0neOAB8ddS0OzM_7QXQDZw"
                , new TemplateMessageStyle() {
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
