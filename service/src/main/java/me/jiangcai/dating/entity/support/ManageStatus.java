package me.jiangcai.dating.entity.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    /**
     * 编辑
     */
    editor,
    /**
     * 客服
     */
    waiter,
    /**
     * 财务
     */
    financial,
    /**
     * 主管
     */
    general,
    /**
     * 经理
     */
    manager,
    /**
     * build-in
     */
    root;


    public List<String> roles() {
        List<String> list = new ArrayList<>();
        list.add(Role_Manage_Value);
        switch (this) {
            case all:
                break;
            case editor:
                list.addAll(Arrays.asList(Role_User_Value, Role_Edit_Value, Role_Agent_Value));
                break;
            case waiter:
                list.addAll(Collections.singletonList((Role_Order_Value)));
                break;
            case financial:
                list.addAll(Arrays.asList(Role_Finance_Value, Role_Loan_Value));
                break;
            case general:
                list.addAll(Arrays.asList(Role_Finance_Value, Role_Order_Value, Role_Edit_Value, Role_Agent_Value));
                break;
            case manager:
                list.addAll(Arrays.asList(Role_Finance_Value, Role_Order_Value, Role_Edit_Value, Role_Agent_Value, Role_Grant_Value));
                break;
            case root:
                list.addAll(Collections.singleton("ROOT"));
                break;
            default:
                throw new IllegalArgumentException("unknown of " + this);
        }
        return list;
    }

    public boolean isRoot() {
        return this == root;
    }

    @Override
    public String toString() {
        switch (this) {
            case all:
                return "观察员";
            case editor:
                return "编辑";
            case waiter:
                return "客服";
            case financial:
                return "财务";
            case general:
                return "主管";
            case manager:
                return "经理";
        }
        return super.toString();
    }
}
