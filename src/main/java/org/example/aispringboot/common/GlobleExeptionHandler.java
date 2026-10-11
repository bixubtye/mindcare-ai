package org.example.aispringboot.common;

import org.example.aispringboot.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobleExeptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleException(MethodArgumentNotValidException e) {
        String message=e.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(","));
        return Result.error(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMsg(), message);
    }
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleException(BusinessException e) {
            if(e.getData() != null) {
                return Result.error(e.getCode(), e.getMessage(), e.getData());
            }
            return Result.error(e.getCode(), e.getMessage(), null);
        }
    }

