package me.jiangai.dating;

import me.jiangcai.wx.PublicAccountSupplier;
import me.jiangcai.wx.model.PublicAccount;

import java.util.Collections;
import java.util.List;

/**
 * @author CJ
 */
public class DebugPublicAccountSupplier implements PublicAccountSupplier {

    private final PublicAccount publicAccount;

    public DebugPublicAccountSupplier(String url) {
        publicAccount = new DebugPublicAccount(url);
    }

    @Override
    public List<PublicAccount> getAccounts() {
        return Collections.singletonList(publicAccount);
    }

    @Override
    public PublicAccount findByIdentifier(String identifier) {
        return publicAccount;
    }

    @Override
    public PublicAccount findByHost(String host) {
        return publicAccount;
    }
}
