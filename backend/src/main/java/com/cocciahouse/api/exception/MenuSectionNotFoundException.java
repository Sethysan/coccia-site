package com.cocciahouse.api.exception;

public class MenuSectionNotFoundException
        extends RuntimeException {

    public MenuSectionNotFoundException(
            String message
    ) {
        super(message);
    }
}