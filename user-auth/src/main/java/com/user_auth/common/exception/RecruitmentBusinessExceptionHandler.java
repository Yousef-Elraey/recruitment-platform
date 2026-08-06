package com.user_auth.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
                .setErrorCode(ex.getErrorCode())
                .setMessage(ex.getMessage())
                .setPath(request.getRequestURI());


        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        ErrorResponseDto response = new ErrorResponseDto()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.BAD_REQUEST.value())
                .setErrorCode(ErrorCode.VALIDATION_ERROR.name())
                .setMessage(message)
                .setPath(request.getRequestURI());

        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponseDto response = new ErrorResponseDto()
                .setTimestamp(LocalDateTime.now())
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setErrorCode(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .setMessage(ex.getMessage())
                .setPath(request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
