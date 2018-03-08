package com.hzz.exception;

/**
 * Created by hongshuiqiao on 2017/6/9.
 */
public class CommonException extends Exception {
    private String code;
    private String errorMessage;

    private CommonException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.errorMessage=message;
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static CommonException customException(String type, int code, String message, Throwable cause) {
        return new CommonException(String.format("%s_%d", type, code), message, cause);
    }
}
