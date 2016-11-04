package me.jiangcai.dating.model.trj;

import lombok.Data;

/**
 * @author CJ
 */
@Data
public class MobileTokenToken {

    private String auth;
    private String pwd;
    private String expire_time;
    private String token;
    private String id;

}
