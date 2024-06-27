package com.example.codingexercise.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public record PackageResponse(String id, String name, String description, List<ProductResponse> products,
                              BigDecimal price, String currency) {
}
