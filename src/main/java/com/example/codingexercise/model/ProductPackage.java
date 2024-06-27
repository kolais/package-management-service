package com.example.codingexercise.model;

import java.util.List;

public record ProductPackage(String id, String name, String description, List<Product> products,
                             int usdTotalPrice) {

    public ProductPackage withId(String id) {
        return new ProductPackage(id, name(), description(), products(), usdTotalPrice());
    }
}
