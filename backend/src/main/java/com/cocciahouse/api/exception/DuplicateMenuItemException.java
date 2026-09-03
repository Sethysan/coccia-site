package com.cocciahouse.api.exception;

public class DuplicateMenuItemException
        extends RuntimeException {

    public DuplicateMenuItemException(
            String message
    ) {
        super(message);
    }
}