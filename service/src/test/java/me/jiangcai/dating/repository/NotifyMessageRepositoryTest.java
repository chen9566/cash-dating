package me.jiangcai.dating.repository;

import me.jiangcai.dating.ServiceBaseTest;
import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.support.NotifyMessagePK;
import me.jiangcai.dating.notify.NotifyType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
public class NotifyMessageRepositoryTest extends ServiceBaseTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private NotifyMessageRepository notifyMessageRepository;

    @Test
    public void hello() {
        notifyMessageRepository.findAll().forEach(System.out::println);

        NotifyType notifyType = NotifyType.values()[random.nextInt(NotifyType.values().length)];
        NotifyMessage message = new NotifyMessage();
        message.setNotifyType(notifyType);
        message.setVersion(notifyType.getLastVersion());
        message.setTemplateIdShort(notifyType.getRecommendShortId());
        message.setTemplateTitle(notifyType.getRecommendTemplateTitle());

        notifyMessageRepository.save(message);

        assertThat(notifyMessageRepository.getOne(new NotifyMessagePK(notifyType, notifyType.getLastVersion())))
                .isNotNull();

        notifyMessageRepository.deleteAll();
    }

}