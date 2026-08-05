package com.user_auth.common.exception;

import org.springframework.http.HttpStatus;

public class RecruitmentBusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public RecruitmentBusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
