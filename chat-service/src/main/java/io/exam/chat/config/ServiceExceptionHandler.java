package io.exam.chat.config;

import io.exam.errors.AuthenticationException;
import io.exam.errors.ServiceException;
import io.exam.rest.ResponseApi;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class ServiceExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public ResponseApi<Void> serviceException(Exception ex, WebRequest request) {
        return ResponseApi.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value=HttpStatus.UNAUTHORIZED)
    public ResponseApi<Void> authenticationException(Exception ex, WebRequest request) {
        return ResponseApi.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    }
}