package com.cloud.ops.common.exception;

public class OpsException extends TechnicalException {
    private static final long serialVersionUID = -5838741067731786413L;
    /** Type of the element not found. */
    private String type;
    /** Id of the element not found. */
    private String id;

    public OpsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpsException(String message) {
        super(message);
    }

    public OpsException(String type, String id, String message) {
        super(message);
        this.type = type;
    }
}
