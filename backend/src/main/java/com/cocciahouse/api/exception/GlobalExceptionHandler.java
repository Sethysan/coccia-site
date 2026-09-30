package com.cocciahouse.api.exception;

import com.cocciahouse.api.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateRecipeException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateRecipe(
            DuplicateRecipeException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler({
            RecipeNotFoundException.class,
            MenuSectionNotFoundException.class,
            MenuItemNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            RuntimeException exception
    ) {

        ApiErrorResponse response =
                new ApiErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        exception.getMessage(),
                        Instant.now()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(
            IllegalStateException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->
                        "%s %s".formatted(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                )
                .orElse("Request validation failed.");

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DuplicateOfferingItemException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateOfferingItem(
            DuplicateOfferingItemException exception
    ) {

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(DuplicateMenuItemException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateMenuItem(
            DuplicateMenuItemException exception
    ) {

        ApiErrorResponse response =
                new ApiErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        exception.getMessage(),
                        Instant.now()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

}