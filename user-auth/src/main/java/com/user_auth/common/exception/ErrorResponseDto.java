package com.user_auth.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ErrorResponseDto {
    private LocalDateTime timestamp;
    private int status;
     private String errorCode;
    private String message;
    private String path;
}
