package com.example.codingexercise.exception;

import static java.lang.String.format;

public class NoPackageFoundException extends RuntimeException {

    public NoPackageFoundException(String id) {
        super(format("Product package with ID '%s' not found", id));
    }
}
