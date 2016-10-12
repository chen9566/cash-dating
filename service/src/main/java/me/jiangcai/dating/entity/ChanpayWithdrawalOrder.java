package me.jiangcai.dating.entity;

import lombok.Getter;
import lombok.Setter;
import me.jiangcai.chanpay.model.WithdrawalStatus;

import javax.persistence.Entity;

/**
 * @author CJ
 */
@Entity
@Setter
@Getter
public class ChanpayWithdrawalOrder extends PlatformWithdrawalOrder {

    //    private WithdrawOrderStatus status;
    private WithdrawalStatus status;

    @Override
    public boolean isFinish() {
        return isSuccess() || status == WithdrawalStatus.WITHDRAWAL_FAIL || status ==
                WithdrawalStatus.RETURN_TICKET;
    }

    @Override
    public boolean isSuccess() {
        return status == WithdrawalStatus.WITHDRAWAL_SUCCESS;
    }
}
