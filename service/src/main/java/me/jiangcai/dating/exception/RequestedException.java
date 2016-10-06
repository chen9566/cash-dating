package me.jiangcai.dating.exception;

/**
 * 已申请
 * @author CJ
 */
public class RequestedException extends RuntimeException {

    public RequestedException(String message) {
        super(message);
    }

    public RequestedException(String message, Throwable cause) {
        super(message, cause);
    }
}
