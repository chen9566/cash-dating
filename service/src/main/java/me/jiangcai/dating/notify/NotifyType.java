package me.jiangcai.dating.notify;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author CJ
 */
public enum NotifyType {

    orderPaid("OPENTM201285651", "订单支付成功通知", "订单已支付", "指的是订单已经确认收款", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("订单备注", String.class)
            , new NotifyParameter("支付金额", String.class)
            , new NotifyParameter("支付时间", String.class)),
    memberRegister("OPENTM207422816", "新合伙人通过加入通知", "合伙人注册", "注册成为你的合伙人的时候", 0
            , new NotifyParameter("会员昵称", String.class)
            , new NotifyParameter("会员手机", String.class)
            , new NotifyParameter("注册时间", String.class)),
    orderTransfer("TM204623114", "到账提醒", "订单转账", "订单金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("手续费", String.class)
            , new NotifyParameter("转账金额", String.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", String.class)),
    withdrawalTransfer("TM204623114", "到账提醒", "提现转账", "提现金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("转账金额", String.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", String.class)),
    orderTransferFailed("TM204623061", "转账失败提醒", "订单转账失败", "订单金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("手续费", String.class)
            , new NotifyParameter("转账金额", String.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", String.class)),
    withdrawalTransferFailed("TM204623061", "转账失败提醒", "提现转账失败", "提现金额开始转入银行卡", 0
            , new NotifyParameter("订单编号", String.class)
            , new NotifyParameter("转账金额", String.class)
            , new NotifyParameter("银行卡号", String.class)
            , new NotifyParameter("转账时间", String.class));

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
            return type == LocalDateTime.class;
        }

        public String getInputType() {
            if (type.isAssignableFrom(Number.class))
                return "number";
            return "text";
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
