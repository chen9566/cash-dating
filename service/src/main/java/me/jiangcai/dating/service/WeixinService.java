package me.jiangcai.dating.service;

import me.jiangcai.wx.model.PublicAccount;

import java.io.IOException;

/**
 * @author CJ
 */
public interface WeixinService {

    /**
     * 使用json格式创建菜单
     *
     * @param json    json字符串
     * @param account 公众帐号
     * @throws IOException
     */
    void menus(String json, PublicAccount account) throws IOException;

}
