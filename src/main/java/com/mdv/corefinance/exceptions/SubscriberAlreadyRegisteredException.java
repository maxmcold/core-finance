package com.mdv.corefinance.exceptions;

public class SubscriberAlreadyRegisteredException extends RuntimeException {
    public SubscriberAlreadyRegisteredException(String exception) {
        super(exception);
    }
}
