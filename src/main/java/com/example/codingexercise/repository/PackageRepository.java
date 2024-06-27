package com.example.codingexercise.repository;

import com.example.codingexercise.model.ProductPackage;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PackageRepository {

    private final Map<String, ProductPackage> productPackages = new ConcurrentHashMap<>();

    public ProductPackage create(ProductPackage productPackage) {
        var newProductPackage = productPackage.withId(RandomStringUtils.randomAlphanumeric(12));
        productPackages.put(newProductPackage.id(), newProductPackage);
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
