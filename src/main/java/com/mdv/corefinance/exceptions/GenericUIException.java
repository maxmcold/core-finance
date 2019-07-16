package com.mdv.corefinance.exceptions;

import com.mdv.corefinance.controllers.LoanRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericUIException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class);

    public GenericUIException(String exception) {
        super(exception);
        logger.error(exception,this);

    }
}
