package me.jiangcai.dating.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.dating.service.WeixinService;
import me.jiangcai.wx.MessageReply;
import me.jiangcai.wx.message.EventMessage;
import me.jiangcai.wx.message.Message;
import me.jiangcai.wx.message.NewsMessage;
import me.jiangcai.wx.message.support.NewsArticle;
import me.jiangcai.wx.message.support.WeixinEvent;
import me.jiangcai.wx.model.Menu;
import me.jiangcai.wx.model.MenuType;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.protocol.Protocol;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @author CJ
 */
@Service
public class WeixinServiceImpl implements WeixinService, MessageReply {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final NewsArticle[] welcomes;

    public WeixinServiceImpl() throws IOException {
        try (InputStream inputStream = new ClassPathResource("/welcome.json").getInputStream()) {
            String json = StreamUtils.copyToString(inputStream, Charset.forName("UTF-8"));
            JsonNode messages;
            try {
                messages = objectMapper.readTree(json);
            } catch (JsonParseException exception) {
                messages = objectMapper.readTree(URLDecoder.decode(json, "UTF-8"));
            }
            welcomes = new NewsArticle[messages.size()];
            for (int i = 0; i < messages.size(); i++) {
                JsonNode one = messages.get(i);
                welcomes[i] = new NewsArticle(one.get("title").asText(), one.get("description").asText()
                        , one.get("imageUrl").asText(), one.get("url").asText());
            }
        }
    }

    @Override
    public void menus(String json, PublicAccount account) throws IOException {

        JsonNode menus;
        try {
            menus = objectMapper.readTree(json);
        } catch (JsonParseException exception) {
            menus = objectMapper.readTree(URLDecoder.decode(json, "UTF-8"));
        }
        // 用简单模式
        // cash.weixin.menus = []
        ArrayList<Menu> menuArrayList = new ArrayList<>();

        for (JsonNode node : menus) {
            Menu menu = fromJson(node);
            menuArrayList.add(menu);
        }

        Protocol.forAccount(account).createMenu(menuArrayList.toArray(new Menu[menuArrayList.size()]));
    }

    private Menu fromJson(JsonNode node) {
        Menu menu = new Menu();
        menu.setName(node.get("name").asText());
        menu.setType(MenuType.valueOf(node.get("type").asText()));
        if (node.has("data")) {
            menu.setData(node.get("data").asText());
        }
        if (node.has("subs")) {
            JsonNode subs = node.get("subs");
            if (subs.isArray()) {
                ArrayList<Menu> menuArrayList = new ArrayList<>();
                for (JsonNode subMenu : subs) {
                    menuArrayList.add(fromJson(subMenu));
                }
                menu.setSubs(menuArrayList.toArray(new Menu[menuArrayList.size()]));
            }
        }
        return menu;
    }

    @Override
    public boolean focus(PublicAccount account, Message message) {
        return message instanceof EventMessage && ((EventMessage) message).getEvent() == WeixinEvent.subscribe;
    }

    @Override
    public Message reply(PublicAccount account, Message message) {
        //NewsArticle
        if (welcomes == null)
            return null;
        return new NewsMessage(welcomes);
    }
}
