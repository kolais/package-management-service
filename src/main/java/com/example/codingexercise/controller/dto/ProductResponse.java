package com.example.codingexercise.controller.dto;

import java.math.BigDecimal;

public record ProductResponse(String id, String name, int quantity, BigDecimal price, String currency) {
}
