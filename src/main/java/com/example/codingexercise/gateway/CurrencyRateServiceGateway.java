package com.example.codingexercise.gateway;

import java.math.BigDecimal;

public interface CurrencyRateServiceGateway {
    BigDecimal getRate(String base, String currency);
}
