package com.example.codingexercise.gateway.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ConversionRates(BigDecimal amount, String base, LocalDate date, Map<String, BigDecimal> rates) {
}
