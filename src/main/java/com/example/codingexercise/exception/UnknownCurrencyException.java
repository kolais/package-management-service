package com.example.codingexercise.exception;

import static java.lang.String.format;

public class UnknownCurrencyException extends RuntimeException {

    public UnknownCurrencyException(String base, String currency) {
        super(format("Unknown currency pair %s/%s", base, currency));
    }
}
