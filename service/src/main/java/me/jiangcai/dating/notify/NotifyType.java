package me.jiangcai.dating.notify;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author CJ
 */
public enum NotifyType {

    orderPaid("OPENTM201285651", "订单支付成功通知", "订单已支付", "指的是订单已经确认收款", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("订单备注", String.class)
            , new NotifyParameter("支付金额", Number.class)
            , new NotifyParameter("支付时间", Date.class)),
    memberRegister("OPENTM207422816", "新合伙人通过加入通知", "合伙人注册", "注册成为你的合伙人的时候", 0
            , new NotifyParameter("会员昵称", String.class)
            , new NotifyParameter("会员手机", String.class)
            , new NotifyParameter("注册时间", Date.class)),
    orderTransfer("TM204623114", "到账提醒", "订单转账", "订单金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("手续费", Number.class)
            , new NotifyParameter("转账金额", Number.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", Date.class)),
    withdrawalTransfer("TM204623114", "到账提醒", "提现转账", "提现金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("转账金额", Number.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", Date.class)),
    orderTransferFailed("TM204623061", "转账失败提醒", "订单转账失败", "订单金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("手续费", Number.class)
            , new NotifyParameter("转账金额", Number.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", Date.class)
            , new NotifyParameter("原因", String.class)),
    withdrawalTransferFailed("TM204623061", "转账失败提醒", "提现转账失败", "提现金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("转账金额", Number.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", Date.class)
            , new NotifyParameter("原因", String.class)),
    projectLoanRejected("OPENTM407618125", "贷款申请进度通知", "项目贷款被拒绝", "项目贷款被拒绝时向用户发送的消息", 0
            , new NotifyParameter("申请人姓名", String.class)
            , new NotifyParameter("业务名称", String.class)
            , new NotifyParameter("申请金额", String.class)
            , new NotifyParameter("审核状态", String.class)),
    projectLoanAccepted("OPENTM407618125", "贷款申请进度通知", "项目贷款被接受", "项目贷款被接受时向用户发送的消息", 0
            , new NotifyParameter("申请人姓名", String.class)
            , new NotifyParameter("业务名称", String.class)
            , new NotifyParameter("申请金额", Number.class)
            , new NotifyParameter("审核状态", String.class)
    );

    private final String recommendShortId;
    private final String recommendTemplateTitle;
    private final String name;
    private final String description;
    private final int lastVersion;
    private final NotifyParameter[] parameters;

    NotifyType(String recommendShortId, String recommendTemplateTitle, String name, String desc, int lastVersion
            , NotifyParameter... parameters) {
        this.recommendShortId = recommendShortId;
        this.recommendTemplateTitle = recommendTemplateTitle;
        this.name = name;
        this.description = desc;
        this.lastVersion = lastVersion;
        this.parameters = parameters;
    }

    public String getRecommendShortId() {
        return recommendShortId;
    }

    public String getRecommendTemplateTitle() {
        return recommendTemplateTitle;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLastVersion() {
        return lastVersion;
    }

    public NotifyParameter[] getParameters() {
        return parameters;
    }

    @Data
    @AllArgsConstructor
    public static class NotifyParameter {
        private String name;
        private Class<?> type;

        public boolean isTimeType() {
            return type == Date.class;
        }

        public String getInputType() {
            if (type == (Number.class))
                return "number";
            return "text";
        }

        public boolean isNumberType() {
            return type == (Number.class);
        }
    }


//    {
//        HttpPost post = null;//
//        HttpEntity entity = EntityBuilder.create()
//                .setContentType(ContentType.APPLICATION_FORM_URLENCODED)
//                .setContentEncoding("UTF-8")
//                .setParameters(new BasicNameValuePair("parameterName","xml Value"))// 此处无需自己encoding
//        .build();
//
//        post.setEntity(entity);
//        //execute..
//    }
}
