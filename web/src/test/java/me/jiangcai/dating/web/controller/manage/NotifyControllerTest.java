package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.AsManage;
import me.jiangcai.dating.ManageWebTest;
import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.NotifyMessageParameter;
import me.jiangcai.dating.entity.support.ManageStatus;
import me.jiangcai.dating.model.NotifyMessageModel;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.page.ManageNotifyPage;
import me.jiangcai.dating.repository.NotifyMessageRepository;
import me.jiangcai.dating.service.NotifyService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author CJ
 */
@AsManage(ManageStatus.editor)
public class NotifyControllerTest extends ManageWebTest {

    @Autowired
    private NotifyService notifyService;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private NotifyMessageRepository notifyMessageRepository;

    @Test
    public void index() {
        driver.get("http://localhost/manage/notify");
        ManageNotifyPage page = initPage(ManageNotifyPage.class);

        // 找一个未曾处理过的notify
        NotifyType type = notifyService.allTemplate().stream()
                .filter(notifyMessageModel -> notifyMessageModel.getMessage() == null)
                .map(NotifyMessageModel::getType)
                .max(new RandomComparator())
                .orElseGet(() -> {
                    notifyMessageRepository.deleteAll();
                    return NotifyType.values()[random.nextInt(NotifyType.values().length)];
                });


        // 开启一个
        page.openNotify(type, false, "", "", "Q_I8jxy9gdhntZg3xNvrn33LLDJDHLHamU1cVszPZqY");

        assertThat(notifyService.allTemplate())
                .extracting("message")
                .areAtLeastOne(new Condition<>(o -> {
                    NotifyMessage message = (NotifyMessage) o;
                    return message != null && message.getNotifyType() == type;
                }, ""));

        // 预览
        // 随机格式
        List<NameValuePair> parameterTemplates = notifyService.allTemplate().stream()
                .filter(notifyMessageModel -> notifyMessageModel.getType() == type)
                .map(NotifyMessageModel::getMessage)
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .getMessageParameters().stream()
                .map(notifyMessageParameter -> {
                    // 自行组装随机的业务参数
                    StringBuilder pattern = new StringBuilder();
                    int index = random.nextInt(type.getParameters().length);
                    NotifyType.NotifyParameter notifyParameter = type.getParameters()[index];
                    pattern.append(RandomStringUtils.randomAscii(4));
                    pattern.append("{").append(index);
                    if (notifyParameter.isTimeType()) {
                        pattern.append(",date}");
                    } else if (notifyParameter.isNumberType()) {
                        pattern.append(",number,currency}");
                    } else
                        pattern.append("}");
                    return (NameValuePair) new BasicNameValuePair(notifyMessageParameter.getName(), pattern.toString());
                }).collect(Collectors.toList());

        page.updateParameterTemplates(type, parameterTemplates);

        // 随机构造业务 并且预览
        page.previewNotify(type, Stream.of(type.getParameters()).map(notifyParameter -> {
            if (notifyParameter.isTimeType())
                return "";
            if (notifyParameter.isNumberType())
                return new BigDecimal(random.nextDouble()).add(new BigDecimal(random.nextInt())).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            return UUID.randomUUID().toString();
        }).collect(Collectors.toList()));
        // 可以的 那就保存吧
        page.saveParameterTemplates(type);
        // 新的模板应该符合我们的要求
        NotifyMessage notifyMessage = notifyService.forType(type);
        assertThat(notifyMessage)
                .isNotNull();
        // 然后查看它的
        assertThat(notifyMessage.getMessageParameters().stream()
                .sorted((o1, o2)
                        -> o1.getName().compareTo(o2.getName()))
                .map(NotifyMessageParameter::getPattern)
                .collect(Collectors.toList())
        ).containsExactlyElementsOf(parameterTemplates.stream()
                .sorted((o1, o2)
                        -> o1.getName().compareTo(o2.getName()))
                .map(NameValuePair::getValue)
                .collect(Collectors.toList()));
    }

    class PFormat {
        private String pattern;
    }

}