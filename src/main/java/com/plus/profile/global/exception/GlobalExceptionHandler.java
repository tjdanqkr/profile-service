package com.plus.profile.global.exception;


import com.plus.profile.global.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("[비즈니스 예외]", e);
        return ResponseEntity
                .status(e.getStatusCode())
                .body(ErrorResponse.of(e.getMessage(), e.getStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ErrorResponse.FieldError.of(
                        err.getField(),
                        err.getRejectedValue(),
                        err.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(
                ErrorResponse.of("Invalid input data", 400, fieldErrors)
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("[예상치 못한 예외]", e);
        return ResponseEntity
                .status(500)
                .body(ErrorResponse.of(GlobalServerException.INTERNAL_SERVER_ERROR.getMessage(), GlobalServerException.INTERNAL_SERVER_ERROR.getStatusCode()));
    }

}
