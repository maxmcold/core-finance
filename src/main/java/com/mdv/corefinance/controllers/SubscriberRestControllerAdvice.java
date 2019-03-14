package com.mdv.corefinance.controllers;


import com.mdv.corefinance.beans.ErrorDetails;
import com.mdv.corefinance.exceptions.GenericRestException;
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
@ControllerAdvice(basePackageClasses = SubscriberRestController.class)
public class SubscriberRestControllerAdvice extends ResponseEntityExceptionHandler {



    @ExceptionHandler(GenericRestException.class)
    ResponseEntity<?> handleGenericException(HttpServletRequest request, Throwable ex) {
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
