package com.cloud.ops.tosca.exception;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/15
 */
public class ToscaParseException extends RuntimeException {
    public ToscaParseException() {
    }

    public ToscaParseException(String message) {
        super(message);
    }

    public ToscaParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToscaParseException(Throwable cause) {
        super(cause);
    }

}
