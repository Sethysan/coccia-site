package com.cocciahouse.api.exception;

public class DuplicateRecipeException extends RuntimeException {

    public DuplicateRecipeException(String message) {
        super(message);
    }
}
