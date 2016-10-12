package me.jiangcai.dating.entity.support;

import java.util.Arrays;
import java.util.List;

import static me.jiangcai.dating.core.Login.*;

/**
 * 管理状态
 * 这个跟我们的用户角色是一一相对的
 * <ul>
 * <li>编辑,还可以审核合伙人</li>
 * <li>客服,查看订单,还有一些应急操作;没有普通用户权限</li>
 * <li>财务,整体流水查看,月度盘账;没有普通用户权限</li>
 * <li>管理,所有权限 包括授权</li>
 * <li>build-in</li>
 * </ul>
 *
 * @author CJ
 */
public enum ManageStatus {
    /**
     * 占位符
     */
    all,
    editor(Role_User_Value, Role_Edit_Value),
    /**
     *
     */
    waiter(Role_Order_Value),
    /**
     * 财务
     */
    financial(Role_Finance_Value),
    /**
     * 主管
     */
    general(Role_Finance_Value, Role_Order_Value, Role_Edit_Value),
    /**
     * 经理
     */
    manager(Role_Finance_Value, Role_Order_Value, Role_Edit_Value, Role_Grant_Value),
    /**
     * build-in
     */
    root("ROOT");


    private final List<String> roles;

    ManageStatus(String... roles) {
        this.roles = Arrays.asList(roles);
        this.roles.add(Role_Manage_Value);
    }

    public List<String> roles() {
        return roles;
    }
}
