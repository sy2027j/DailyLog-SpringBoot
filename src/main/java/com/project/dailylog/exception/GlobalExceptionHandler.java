package com.project.dailylog.exception;

import com.project.dailylog.service.ResponseService;
import com.project.dailylog.model.response.ErrorResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final ResponseService responseService;

    @ExceptionHandler(DuplicateEmailException.class)
    public ErrorResult handleDuplicateEmailException(DuplicateEmailException ex) {
        return responseService.getErrorResult(
                HttpStatus.BAD_REQUEST.value(),
                "EMAIL_ALREADY_EXISTS",
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleGeneralException(Exception ex) {
        ErrorResult errorResult = responseService.getErrorResult(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "알 수 없는 오류가 발생했습니다.",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
