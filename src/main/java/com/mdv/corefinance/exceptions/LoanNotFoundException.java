package com.mdv.corefinance.exceptions;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(String message){
        super(message);
    }
}
