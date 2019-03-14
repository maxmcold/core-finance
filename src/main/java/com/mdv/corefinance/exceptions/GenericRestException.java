package com.mdv.corefinance.exceptions;

public class GenericRestException extends RuntimeException {
    public GenericRestException(String exception) {
        super(exception);
    }
}
