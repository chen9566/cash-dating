package me.jiangcai.dating.model.trj;

/**
 * 固定的借款项目,叫做项目贷款
 *
 * @author CJ
 */
public class ProjectLoan extends Loan {

    public static final String ProjectLoanID = "ProjectLoanID";

    public ProjectLoan() {
        setProductId(ProjectLoanID);
        setProductName("项目贷款");
        setAmount("20万");
        setCondition("");
        setIntroduce("本借款项目系为具有个人消费需求的汽车车主，提供的短期消费类借款产品。该产品由作为借款人的汽车车主，将其的自有车辆作为质押物申请借款。经过投融家平台审核后，确认借款人资信良好，车辆权属清晰的，且借款人所有车辆的可以依法质押的，方允许借款人发布借款信息。借款项目由第三方担保公司提供第三方连带责任保证，并且借款金额根据评估价值进行相应的折扣。本产品具有借款申请快捷，借款期限短、借款保障系数高，质押车辆处置快速的特点。");
        setTerm(new String[]{"12月"});
    }
}
