package me.jiangcai.dating.web.controller.manage;

import me.jiangcai.dating.core.Login;
import me.jiangcai.dating.entity.NotifyMessage;
import me.jiangcai.dating.entity.NotifyMessageParameter;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.notify.NotifyType;
import me.jiangcai.dating.service.NotifyService;
import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.Template;
import me.jiangcai.wx.protocol.Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理通知的
 *
 * @author CJ
 */
@PreAuthorize("hasAnyRole('ROOT','" + Login.Role_Edit_Value + "')")
@Controller
@RequestMapping("/manage/notify")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;
    @Autowired
    private PublicAccountSupplier supplier;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("templateMessages", notifyService.allTemplate());
        return "manage/notify.html";
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public String change(boolean enabled, String type, String title, String shortId, String templateId) {
        // 看看自己有没有
        NotifyType notifyType = NotifyType.valueOf(type);
        NotifyMessage message = notifyService.forType(notifyType);

        // 如果是新的
        if (message == null) {
            message = new NotifyMessage();
            apply(message, title, shortId, templateId);
            message.setNotifyType(notifyType);
            message.setVersion(notifyType.getLastVersion());
            notifyService.save(message);
        } else {
            // 如果是已经存在的,需要先暂时禁用
            message.setEnabled(enabled);
            // 检查下 有没有改动
            if (changed(message, title, shortId, templateId)) {
                String originId = message.getTemplateId();
                Set<NotifyMessageParameter> messageParameters = Collections.unmodifiableSet(message.getMessageParameters());
                apply(message, title, shortId, templateId);

                if (message.getTemplateId() != null && message.getTemplateId().equals(originId)) {
                    // 留下之前的配置
                    message.getMessageParameters().forEach(notifyMessageParameter
                            -> messageParameters.stream()
                            .filter(notifyMessageParameter1
                                    -> notifyMessageParameter1.getName().equals(notifyMessageParameter.getName()))
                            .findFirst()
                            .ifPresent(notifyMessageParameter1 -> {
                                notifyMessageParameter.setPattern(notifyMessageParameter1.getPattern());
                                notifyMessageParameter.setDefaultColor(notifyMessageParameter1.getDefaultColor());
                            }));
                }

            }
        }


        return "redirect:/manage/notify";
    }

    private boolean changed(NotifyMessage message, String title, String shortId, String templateId) {
        if (!StringUtils.isEmpty(title) && !title.equals(message.getTemplateTitle()))
            return true;
        if (!StringUtils.isEmpty(shortId) && !shortId.equals(message.getTemplateIdShort()))
            return true;
        if (!StringUtils.isEmpty(templateId) && !templateId.equals(message.getTemplateId()))
            return true;
        return false;
    }

    private void apply(NotifyMessage message, String title, String shortId, String templateId) {
        Protocol protocol = Protocol.forAccount(supplier.findByIdentifier(null));

        if (!StringUtils.isEmpty(title))
            message.setTemplateTitle(title);
        if (!StringUtils.isEmpty(shortId))
            message.setTemplateIdShort(shortId);
        if (!StringUtils.isEmpty(templateId))
            message.setTemplateId(templateId);
        if (message.getMessageParameters() == null) {
            message.setMessageParameters(new HashSet<>());
        }

        Template template = protocol.getTemplate(message);
        message.setMessageParameters(template.parameters().stream()
                .map(name -> {
                    NotifyMessageParameter parameter = new NotifyMessageParameter();
                    parameter.setName(name);
                    return parameter;
                })
                .collect(Collectors.toSet()));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/parameters")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void parameters(@RequestBody Map<String, Object> data) {
        NotifyType notifyType = NotifyType.valueOf(data.get("type").toString());
        NotifyMessage message = notifyService.forType(notifyType);

        data.keySet().forEach(key
                -> message.getMessageParameters().stream().filter(notifyMessageParameter
                -> notifyMessageParameter.getName().equals(key))
                .findFirst()
                .ifPresent(notifyMessageParameter
                        -> notifyMessageParameter.setPattern(data.get(key).toString())));
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/preview")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public void preview(@AuthenticationPrincipal User user, @RequestBody Map<String, Object> data) {
        NotifyType notifyType = NotifyType.valueOf(data.get("type").toString());
        NotifyMessage message = notifyService.forType(notifyType);

        //模板
        Set<NotifyMessageParameter> parameters = clone(message.getMessageParameters());
        data.keySet().forEach(key
                -> parameters.stream().filter(notifyMessageParameter
                -> notifyMessageParameter.getName().equals(key))
                .findFirst()
                .ifPresent(notifyMessageParameter
                        -> notifyMessageParameter.setPattern(data.get(key).toString())));
        //业务参数
        ArrayList<Object> vars = new ArrayList<>();
        for (int i = 0; i < notifyType.getParameters().length; i++) {
            NotifyType.NotifyParameter parameter = notifyType.getParameters()[i];
            if (parameter.isTimeType())
                vars.add(LocalDateTime.now());
            else {
                String input = (String) data.get("" + i);
                if (input == null) {
                    vars.add(parameter.isNumberType() ? 0 : "");
                } else {
                    if (parameter.isNumberType()) {
                        vars.add(NumberUtils.parseNumber(input, BigDecimal.class));
                    } else
                        vars.add(input);
                }
            }
        }
        //发送消息
        notifyService.sendMessage(user, null, message, parameters, vars.toArray(new Object[vars.size()]));
    }

    private Set<NotifyMessageParameter> clone(Set<NotifyMessageParameter> input) {
        HashSet<NotifyMessageParameter> set = new HashSet<>();
        input.forEach(notifyMessageParameter -> {
            NotifyMessageParameter notifyMessageParameter1 = new NotifyMessageParameter();
            notifyMessageParameter1.setDefaultColor(notifyMessageParameter.getDefaultColor());
            notifyMessageParameter1.setPattern(notifyMessageParameter.getPattern());
            notifyMessageParameter1.setName(notifyMessageParameter.getName());
            set.add(notifyMessageParameter1);
        });
        return set;
    }


}
