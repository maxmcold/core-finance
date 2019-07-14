package com.mdv.corefinance.exceptions;

public class BalanceNotAvailableException extends RuntimeException {
    public BalanceNotAvailableException(String exception) {
        super(exception);
    }
}
