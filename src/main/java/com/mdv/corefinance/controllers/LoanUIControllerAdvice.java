package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.ErrorDetails;
import com.mdv.corefinance.exceptions.AccountNotFoundException;
import com.mdv.corefinance.exceptions.GenericUIException;
import com.mdv.corefinance.exceptions.LoanNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackageClasses = LoanUIController.class)
public class LoanUIControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleLoanNotFoundException(LoanNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = getStatus(request);

        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
                request.getServletPath(), status);
        ResponseEntity<ErrorDetails> errors =  new ResponseEntity<>(errorDetails, status);

        return errors;
    }

    @ExceptionHandler(GenericUIException.class)
    ResponseEntity<?> handleGenericException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
                request.getQueryString());
        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<?> handleAccountNotFoundException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
                request.getQueryString());
        return new ResponseEntity<>(errorDetails, status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.valueOf(statusCode);
    }



}
