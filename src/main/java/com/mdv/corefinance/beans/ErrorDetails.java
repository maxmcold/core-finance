package com.mdv.corefinance.beans;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;


import java.util.Date;

@Document
public class ErrorDetails  {


    public Date timestamp;
    public String message;
    public String details;
    public HttpStatus status;

    public ErrorDetails(Date timestamp, String message, String details, HttpStatus status) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.status = status;
    }

    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;

    }



}
