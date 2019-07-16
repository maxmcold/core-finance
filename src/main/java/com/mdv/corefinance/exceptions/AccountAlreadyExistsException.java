package com.mdv.corefinance.exceptions;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String exception) {
        super(exception);
    }
}
