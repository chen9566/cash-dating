package me.jiangcai.dating.model.trj;

/**
 * @author CJ
 */
public class VerifyCodeSentException extends Exception {

    private final MobileToken token;

    public VerifyCodeSentException(MobileToken token) {
        this.token = token;
    }

    public MobileToken getToken() {
        return token;
    }
}
