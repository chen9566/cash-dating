package me.jiangcai.dating.service.impl;

import me.jiangcai.dating.entity.Bank;
import me.jiangcai.dating.entity.Card;
import me.jiangcai.dating.entity.SubBranchBank;
import me.jiangcai.dating.entity.User;
import me.jiangcai.dating.entity.UserOrder;
import me.jiangcai.dating.entity.support.Address;
import me.jiangcai.dating.repository.SubBranchBankRepository;
import me.jiangcai.dating.repository.UserRepository;
import me.jiangcai.dating.service.CardService;
import me.jiangcai.dating.service.PayResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author CJ
 */
@Service
public class CardServiceImpl implements CardService {

    private static final Log log = LogFactory.getLog(CardServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubBranchBankRepository subBranchBankRepository;


    @Override
    public boolean bankAccountRequired(String openId) {
        User user = userRepository.findByOpenId(openId);
        if (user == null)
            return true;
        return user.getCards() == null || user.getCards().isEmpty();
    }

    @Override
    public Card addCard(String openId, String name, String number, Bank bank, Address address, String subBranch) {
//        verificationCodeService.verify(mobile, code, VerificationType.card);

        User user = userRepository.findByOpenId(openId);

        SubBranchBank branchBank = subBranchBankRepository.getOne(subBranch);

        Card card = new Card();
        card.setNumber(number);
        card.setOwner(name);
        card.setBank(branchBank.getBank());

        Address address1 = new Address();
        address1.setCity(PayResourceService.cityById(branchBank.getCityCode()));
        address1.setProvince(PayResourceService.provinceByCity(address1.getCity()));

        card.setAddress(address1);

        card.setSubBranchBank(branchBank);
        card.setSubBranch(branchBank.getName());
//        card.setSubBranch(subBranch);

        if (user.getCards() == null) {
            user.setCards(new ArrayList<>());
        }
        user.getCards().add(card);
        user = userRepository.save(user);
        return user.getCards().stream()
                .filter(card1 -> card1.getNumber().equals(number))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }


    @Override
    public void deleteCards(String openId) {
        User user = userRepository.findByOpenId(openId);

        if (user.getCards() != null)
            user.getCards().clear();
    }

    @Override
    public Card recommend(UserOrder order) {
        if (order.getCard() != null && !order.getCard().isDisabled())
            return order.getCard();
        if (order.getOwner().getCards() == null)
            return null;
        return recommend(order.getOwner());
    }

    @Override
    public Card recommend(User user) {
        return user.getCards().stream()
                .filter(card -> !card.isDisabled())
                .findFirst()
                .orElse(null);
    }


}
