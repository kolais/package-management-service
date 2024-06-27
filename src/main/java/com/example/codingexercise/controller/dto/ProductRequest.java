package com.example.codingexercise.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record ProductRequest(
        @NotEmpty(message = "Product ID cannot be empty.")
        String id,
        @Min(value = 1, message = "Product quantity cannot be less than 1.")
        int quantity) {
}
