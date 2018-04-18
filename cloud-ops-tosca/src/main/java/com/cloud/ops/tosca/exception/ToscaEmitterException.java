package com.cloud.ops.tosca.exception;

/**
 * @author ningsheng
 * @version 1.0
 * @date 2017/8/15
 */
public class ToscaEmitterException extends RuntimeException {
    public ToscaEmitterException() {
    }

    public ToscaEmitterException(String message) {
        super(message);
    }

    public ToscaEmitterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ToscaEmitterException(Throwable cause) {
        super(cause);
    }

}
