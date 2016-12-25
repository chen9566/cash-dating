package me.jiangcai.dating.model;

import lombok.Data;

/**
 * 邀请的用户
 *
 * @author CJ
 */
@Data
public class InviteUser {

    private final String nickname;
    private final String headImageUrl;
    private final boolean payed;

    public InviteUser(String nickname, String headImageUrl, long count) {
        this.nickname = nickname;
        this.headImageUrl = headImageUrl;
        this.payed = count >= 1;
    }

    public InviteUser(String nickname, String headImageUrl, boolean payed) {
        this.nickname = nickname;
        this.headImageUrl = headImageUrl;
        this.payed = payed;
    }
}
