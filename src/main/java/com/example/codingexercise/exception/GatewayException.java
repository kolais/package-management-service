package com.example.codingexercise.exception;

public class GatewayException extends RuntimeException {

    public GatewayException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
