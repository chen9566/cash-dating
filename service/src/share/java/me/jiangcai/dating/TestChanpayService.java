package me.jiangcai.dating;

import me.jiangcai.chanpay.data.trade.CreateInstantTrade;
import me.jiangcai.dating.entity.CashOrder;
import me.jiangcai.dating.entity.ChanpayOrder;
import me.jiangcai.dating.service.BankService;
import me.jiangcai.dating.service.impl.AbstractChanpayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author CJ
 */
public class TestChanpayService extends AbstractChanpayService {

    @Autowired
    private BankService bankService;

    @Override
    public String QRCodeImageFromOrder(ChanpayOrder order) throws IllegalStateException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://localhost/qrUrl?url=")
                .append(URLEncoder.encode(order.getUrl(), "UTF-8"));
        return stringBuilder.toString();
    }

    @Override
    protected void beforeExecute(CashOrder order, CreateInstantTrade request) {
        request.setBankCode("WXPAY");
    }

//    @Override
//    protected void beforeExecuteWithdrawal(UserOrder order, ChanpayWithdrawalOrder withdrawalOrder, Card card) {
//        // 为了确保提现成功 我们使用测试的数据
//        Address address = new Address();
//        address.setProvince(Dictionary.findByName(Province.class, "上海市"));
//        address.setCity(address.getProvince().getCityList().stream()
//                .filter(city -> city.getName().equals("上海市"))
//                .findAny()
//                .orElse(null));
//
//        withdrawalOrder.setAddress(address);
//
//        withdrawalOrder.setBank(bankService.byName("招商银行"));
//        withdrawalOrder.setSubBranch("中国招商银行上海市浦建路支行");
//        withdrawalOrder.setOwner("测试01");
//        withdrawalOrder.setNumber("6214830215878947");
//    }
}
