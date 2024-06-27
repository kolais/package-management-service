package com.example.codingexercise.controller.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PackageRequest(
        @NotEmpty(message = "Package name cannot be empty.")
        String name,
        String description,
        @NotEmpty(message = "Package products list cannot be empty.")
        List<ProductRequest> products) {
}
