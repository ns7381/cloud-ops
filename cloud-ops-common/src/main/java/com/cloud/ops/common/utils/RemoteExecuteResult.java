package com.cloud.ops.common.utils;

/**
 * Created by Administrator on 2017/2/16.
 */
public class RemoteExecuteResult {
    private String message;
    private Integer exitCode;

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
