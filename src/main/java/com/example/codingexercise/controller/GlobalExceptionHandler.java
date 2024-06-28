package com.example.codingexercise.controller;

import com.example.codingexercise.controller.dto.ErrorResponse;
import com.example.codingexercise.exception.NoPackageFoundException;
import com.example.codingexercise.exception.UnknownCurrencyException;
import com.example.codingexercise.exception.UnknownProductException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static java.util.stream.Collectors.joining;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LogManager.getLogger();

    @ExceptionHandler(NoPackageFoundException.class)
    public ResponseEntity<ErrorResponse> packageNotFoundHandler(NoPackageFoundException exception) {
        logger.error("Error caught", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(UnknownProductException.class)
    public ResponseEntity<ErrorResponse> unknownProductHandler(UnknownProductException exception) {
        logger.error("Error caught", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(UnknownCurrencyException.class)
    public ResponseEntity<ErrorResponse> unknownCurrencyHandler(UnknownCurrencyException exception) {
        logger.error("Error caught", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noEndpointHandler(NoResourceFoundException exception) {
        logger.error("Error caught", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ErrorResponse> messageConversionErrorHandler(HttpMessageConversionException exception) {
        logger.error("Error caught", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), exception.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> bindErrorHandler(BindException exception) {
        logger.error("Error caught", exception);
        var message = exception.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(joining(","));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(exception.getClass().getSimpleName(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> defaultHandler(Exception exception) {
        logger.error("Error caught", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(exception.getClass().getName(), exception.getMessage()));
    }
}
