package com.example.codingexercise.repository;

import com.example.codingexercise.model.ProductPackage;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PackageRepository {

    private final Map<String, ProductPackage> productPackages = new ConcurrentHashMap<>();

    public ProductPackage create(String name, String description, List<String> productIds) {
        ProductPackage newProductPackage = new ProductPackage(RandomStringUtils.randomAlphanumeric(12), name, description, productIds);
        productPackages.put(newProductPackage.getId(), newProductPackage);
        return newProductPackage;
    }

    public ProductPackage get(String id) {
        return productPackages.get(id);
    }

    public void delete(String id) {
        productPackages.remove(id);
    }

    public Collection<ProductPackage> getAll() {
        return productPackages.values();
    }
}
