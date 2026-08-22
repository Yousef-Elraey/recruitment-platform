package com.application_service.common.exceprion;

import org.springframework.http.HttpStatus;

public class RecruitmentBusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public RecruitmentBusinessException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
