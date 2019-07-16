package com.mdv.corefinance.exceptions;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String exception) {
        super(exception);
    }
}
