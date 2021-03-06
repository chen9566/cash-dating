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
        setProductName("e商贷");
        setAmount("20万");
        setCondition("");
        setIntroduce("个人网店经营性借款");
        setTerm(new String[]{"30", "60", "90", "180", "365"});
    }

    @Override
    public int getMinAmount() {
        return 1;
    }
}
