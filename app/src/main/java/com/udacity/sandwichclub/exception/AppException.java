package com.udacity.sandwichclub.exception;

public class AppException extends RuntimeException {

    private String mCode;

    public AppException(String code) {
        this.mCode = code;
    }

    public AppException(String code, Throwable throwable) {
        super(throwable);
        this.mCode = code;
    }

    public String getCode() {
        return mCode;
    }
}
