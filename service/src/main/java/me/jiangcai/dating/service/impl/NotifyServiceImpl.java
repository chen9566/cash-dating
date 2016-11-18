package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.support.NotifyMessagePK;
import me.jiangcai.dating.model.NotifyMessageModel;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.repository.NotifyMessageRepository;
import me.jiangcai.dating.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author CJ
 */
@Service
public class NotifyServiceImpl implements NotifyService {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private NotifyMessageRepository notifyMessageRepository;

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
}
