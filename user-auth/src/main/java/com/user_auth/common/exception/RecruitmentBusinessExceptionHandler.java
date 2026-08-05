package com.user_auth.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RecruitmentBusinessExceptionHandler {
    @ExceptionHandler(RecruitmentBusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(
            RecruitmentBusinessException ex,
            HttpServletRequest request) {

        ErrorResponseDto response = new ErrorResponseDto();
        response.setTimestamp(LocalDateTime.now())
                .setStatus(ex.getStatus().value())
                .setError(ex.getCode())
                .setMessage(ex.getMessage())
                .setPath(request.getRequestURI());


        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }
}
