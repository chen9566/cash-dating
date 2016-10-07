package me.jiangcai.dating.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.jiangcai.dating.service.WeixinService;
import me.jiangcai.wx.model.Menu;
import me.jiangcai.wx.model.MenuType;
import me.jiangcai.wx.model.PublicAccount;
import me.jiangcai.wx.protocol.Protocol;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * @author CJ
 */
@Service
public class WeixinServiceImpl implements WeixinService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

}
