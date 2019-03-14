package com.mdv.corefinance.exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String exception) {
        super(exception);
    }
}
