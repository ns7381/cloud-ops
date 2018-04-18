package com.cloud.ops.tosca.exception;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/16
 */
public class ToscaException extends RuntimeException {
    public ToscaException() {
    }

    public ToscaException(String message) {
        super(message);
    }

    public ToscaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToscaException(Throwable cause) {
        super(cause);
    }
}
