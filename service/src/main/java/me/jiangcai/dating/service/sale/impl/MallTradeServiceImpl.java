package me.jiangcai.dating.service.sale.impl;

import me.jiangcai.dating.entity.sale.CashTrade;
import me.jiangcai.dating.repository.sale.CashTradeRepository;
import me.jiangcai.dating.service.sale.MallTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author CJ
 */
@Service("mallTradeService")
public class MallTradeServiceImpl implements MallTradeService {

    @Autowired
    private CashTradeRepository cashTradeRepository;

    @Override
    public CashTrade trade(long id) {
        return cashTradeRepository.getOne(id);
    }
}
