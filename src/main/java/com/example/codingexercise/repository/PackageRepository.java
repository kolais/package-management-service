package com.example.codingexercise.repository;

import com.example.codingexercise.exception.NoPackageFoundException;
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
        checkPackageExists(id);
        return productPackages.get(id);
    }

    public ProductPackage update(String id, ProductPackage productPackage) {
        checkPackageExists(id);
        var newProductPackage = productPackage.withId(id);
        productPackages.put(id, newProductPackage);
        return newProductPackage;
    }

    public void delete(String id) {
        checkPackageExists(id);
        productPackages.remove(id);
    }

    public Collection<ProductPackage> getAll() {
        return productPackages.values();
    }

    private void checkPackageExists(String id) {
        if (!productPackages.containsKey(id)) {
            throw new NoPackageFoundException(id);
        }
    }
}
