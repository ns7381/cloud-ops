package com.cloud.ops.tosca.exception;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/15
 */
public class ToscaParseKeyException extends RuntimeException {
    public ToscaParseKeyException() {
    }

    public ToscaParseKeyException(String message) {
        super("can not parse key: " + message);
    }

    public ToscaParseKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToscaParseKeyException(Throwable cause) {
        super(cause);
    }

}
