package com.example.codingexercise.exception;

import static java.lang.String.format;

public class UnknownProductException extends RuntimeException {

    public UnknownProductException(String productId) {
        super(format("Unknown product with ID '%s'", productId));
    }
}
