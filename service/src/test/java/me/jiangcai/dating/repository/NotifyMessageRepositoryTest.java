package me.jiangcai.dating.repository;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.NotifyMessageParameter;
import me.jiangcai.dating.entity.support.NotifyMessagePK;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.service.NotifyService;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class NotifyMessageRepositoryTest extends ServiceBaseTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private NotifyMessageRepository notifyMessageRepository;
    @Autowired
    private NotifyService notifyService;

    @Test
    public void hello() {
        assertThat(notifyService.allTemplate())
                .hasSize(NotifyType.values().length);

        notifyMessageRepository.findAll().forEach(System.out::println);

        NotifyType notifyType = NotifyType.values()[random.nextInt(NotifyType.values().length)];
        NotifyMessage message = new NotifyMessage();
        message.setNotifyType(notifyType);
        message.setVersion(notifyType.getLastVersion());
        message.setTemplateIdShort(notifyType.getRecommendShortId());
        message.setTemplateTitle(notifyType.getRecommendTemplateTitle());
        final NotifyMessageParameter parameter = new NotifyMessageParameter();
        parameter.setName("test");
        parameter.setDefaultColor(Color.cyan);
        message.setMessageParameters(new HashSet<>());
        message.getMessageParameters().add(parameter);

        notifyMessageRepository.save(message);

        assertThat(notifyMessageRepository.getOne(new NotifyMessagePK(notifyType, notifyType.getLastVersion())))
                .isNotNull();
        assertThat(notifyService.allTemplate())
                .hasSize(NotifyType.values().length)
                .extracting("message")
                .areExactly(1, new Condition<>(o -> o != null, ""))
        ;

        notifyMessageRepository.deleteAll();
    }

}